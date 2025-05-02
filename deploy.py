import glob
import os
import subprocess
import paramiko
from time import sleep
from dotenv import load_dotenv
from tqdm import tqdm

# 从 .env 文件加载环境变量
load_dotenv(override=True)

# 配置参数从环境变量中读取
maven_command = os.getenv("MAVEN_COMMAND")
modules = os.getenv("MODULES").split(",")
remote_jar_path = os.getenv("REMOTE_JAR_PATH")
remote_server_ip = os.getenv("REMOTE_SERVER_IP")
remote_server_port = int(os.getenv("REMOTE_SERVER_PORT", 22))
remote_username = os.getenv("REMOTE_USERNAME")
remote_password = os.getenv("REMOTE_PASSWORD")
jvm_params = os.getenv("JVM_PARAMS")

local_jars = []

def load_jars():
    global local_jars
    local_jars = []
    for item in modules:
        local_dir = f"{item}/target"  # 每个模块的目标文件夹
        # 使用 glob 查找文件，匹配以 item 开头，.jar 结尾的文件
        jar_files = glob.glob(f"{local_dir}/{item}-*.jar")

        if not jar_files:  # 如果没有找到文件
            print(f"没有找到 {item} 的 JAR 文件!")
            continue  # 跳过当前模块，继续处理下一个模块
        local_jars.append(jar_files[0])

    if not local_jars:
        print("没有找到任何 JAR 文件！")
        exit(1)

def run_maven_build():
    """运行 Maven 打包命令"""
    for item in modules:
        print(f"Start Build: {item}")
        result = subprocess.run(f"{maven_command} -pl {item} -am", shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        if result.returncode == 0:
            print(f"Build Success: {item}!")
        else:
            print(f"Build Fail: {item}!")
            print(result.stderr.decode())
            exit(1)

def progress(bytes_sent, bytes_total):
    """上传进度条回调"""
    progress_bar.update(bytes_sent - progress_bar.n)

def upload_jar_to_server(remote_path):
    """将 JAR 文件上传到远程服务器"""
    print(f"Uploading JAR to server: {remote_server_ip}...")

    # 创建 SSH 客户端连接
    ssh_client = paramiko.SSHClient()
    # 启用调试信息
    ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh_client.connect(remote_server_ip, username=remote_username, password=remote_password, port=remote_server_port)
    sftp = ssh_client.open_sftp()


    for local_jar in local_jars:
        try:
            remote_jar_path = f"{remote_path}/{local_jar.split('/')[-1]}"
            print(f"开始上传 {local_jar.split('/')[-1]} 到 {remote_jar_path}...")

            # 检查目标文件是否已存在，如果存在则删除
            try:
                sftp.stat(remote_jar_path)  # 检查文件是否存在
                print(f"目标文件 {remote_jar_path} 已存在，准备覆盖...")
                sftp.remove(remote_jar_path)  # 删除文件
            except FileNotFoundError:
                print(f"目标文件 {remote_jar_path} 不存在，直接上传...")

            # 获取文件大小
            file_size = os.path.getsize(local_jar)

            # 创建进度条
            global progress_bar
            progress_bar = tqdm(total=file_size, unit='B', unit_scale=True, desc=f'上传 {local_jar.split("/")[-1]}')

            # 上传文件并显示进度条
            sftp.put(local_jar, remote_jar_path, callback=lambda sent, total: progress(sent, total))  # 上传文件并显示进度条

            print(f"{local_jar.split('/')[-1]} 上传成功！")
        except Exception as e:
            print(f"上传 JAR 包失败: {e}")
            # 关闭 SFTP 和 SSH 连接
            sftp.close()
            ssh_client.close()
            exit(1)

    # 关闭 SFTP 和 SSH 连接
    sftp.close()
    ssh_client.close()

def stop_old_jar():
    """停止旧的 JAR 包并启动新的 JAR 包"""

    # 创建 SSH 客户端连接
    ssh_client = paramiko.SSHClient()
    ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh_client.connect(remote_server_ip, username=remote_username, password=remote_password, port=remote_server_port)

    for local_jar in local_jars:
        try:
            jar = local_jar.split('/')[-1]
            # 查找正在运行的 JAR 包进程
            stop_command = f"ps aux | grep '{jar}' | grep -v 'grep' | awk '{{print $2}}'"
            stdin, stdout, stderr = ssh_client.exec_command(stop_command)
            pid = stdout.read().decode().strip()  # 获取进程 ID
            
            if pid:
                # 如果找到了 PID，执行杀死进程命令
                kill_command = f"kill -9 {pid}"
                stdin, stdout, stderr = ssh_client.exec_command(kill_command)
                print(f"Stop {jar} with PID {pid}")
                
                # 等待命令执行完成
                exit_status = stdout.channel.recv_exit_status()  # 获取命令执行结果
                if exit_status != 0:
                    print(f"停止进程时出错，错误信息: {stderr.read().decode()}")
                else:
                    print(f"旧进程停止成功: {stdout.read().decode()}")
            else:
                print(f"没有找到 {jar} 进程，跳过停止操作。")
        except Exception as e:
            print(f"Run {jar} Fail: {e}")
            ssh_client.close()
            exit(1)

    # 关闭 SSH 客户端
    ssh_client.close()

def start_new_jar():
    # 创建 SSH 客户端连接
    ssh_client = paramiko.SSHClient()
    ssh_client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh_client.connect(remote_server_ip, username=remote_username, password=remote_password, port=remote_server_port)

    for local_jar in local_jars:
        try:
            jar = local_jar.split('/')[-1]

            # 启动新的 JAR 包
            start_command = f"nohup java {jvm_params} -jar {remote_jar_path}/{jar} --spring.profiles.active=prod &"
            stdin, stdout, stderr = ssh_client.exec_command(start_command)
            print(f"Run {jar} Success!")
        except Exception as e:
            print(f"Run {jar} Fail: {e}")
            ssh_client.close()
            exit(1)

    # 关闭 SSH 客户端
    ssh_client.close()


if __name__ == "__main__":
    load_jars()
    # 执行 Maven 打包
#     run_maven_build()
    
    # 上传 JAR 包到服务器
#     upload_jar_to_server(remote_jar_path)
    
    # 停止旧的 JAR 包并启动新的 JAR 包
    stop_old_jar()
#     sleep(1)
#     start_new_jar()