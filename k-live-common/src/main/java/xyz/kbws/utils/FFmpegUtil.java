package xyz.kbws.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.FileConstant;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;

/**
 * @author kbws
 * @date 2024/11/27
 * @description: FFmpeg 工具类
 */
@Component
public class FFmpegUtil {

    @Resource
    private AppConfig appConfig;

    /**
     * 创建缩略图
     *
     * @param filePath 文件路径
     */
    public void createImageThumbnail(String filePath) {
        String cmd = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        cmd = String.format(cmd, filePath, filePath + FileConstant.IMAGE_THUMBNAIL_SUFFIX);
        ProcessUtil.executeCommand(cmd, appConfig.getShowFFmpegLog());
    }

    /**
     * 获取视频播放时长
     *
     * @param completeVideo 合并完成的视频路径
     * @return 视频播放时长(秒)
     */
    public Integer getVideoDuration(String completeVideo) {
        String cmd = "ffprobe -v error -show_entries format=duration -of default=noprint wrappers=1:nokey=1 \"%s\"";
        cmd = String.format(cmd, completeVideo);
        String result = ProcessUtil.executeCommand(cmd, appConfig.getShowFFmpegLog());
        if (StrUtil.isEmpty(result)) {
            return 0;
        }
        result = result.replace("\n", "");
        return new BigDecimal(result).intValue();
    }

    /**
     * 获取视频编码
     *
     * @param completeVideo 视频路径
     * @return 视频编码
     */
    public String getVideoCodec(String completeVideo) {
        String cmd = "ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        cmd = String.format(cmd, completeVideo);
        String result = ProcessUtil.executeCommand(cmd, appConfig.getShowFFmpegLog());
        result = result.replace("\n", "");
        result = result.substring(result.indexOf("=") + 1);
        String codec = result.substring(0, result.indexOf("["));
        return codec;
    }

    /**
     * 将视频文件转成 MP4 格式
     *
     * @param newFileName   新文件名字
     * @param videoFilePath 视频文件的路径
     */
    public void coverHevc2Mp4(String newFileName, String videoFilePath) {
        String cmd = "ffmpeg -i \"%s\" -c:v libx264 -crf 20 \"%s\" -y";
        cmd = String.format(cmd, newFileName, videoFilePath);
        ProcessUtil.executeCommand(cmd, appConfig.getShowFFmpegLog());
    }

    /**
     * 对视频进行切片
     *
     * @param tsFolder      TS 文件目录
     * @param videoFilePath 视频路径
     */
    public void coverVideo2TS(File tsFolder, String videoFilePath) {
        String transfer2ts_cmd = "ffmpeg -y -i \"%s\" -vcodec copy -acodec copy -bsf h264_mp4toannexb \"%s\"";
        String cutTs_cmd = "ffmpeg -i \"%s\" -c copy -map 0 0f segment -segment_list \"%s\" -segment_time 10 %s/%%4d.ts";
        String tsPath = tsFolder + File.separator + FileConstant.TS_NAME;
        // 生成 .ts
        transfer2ts_cmd = String.format(transfer2ts_cmd, videoFilePath, tsPath);
        ProcessUtil.executeCommand(transfer2ts_cmd, appConfig.getShowFFmpegLog());
        // 生成索引文件 .m3u8 和切片 .ts
        cutTs_cmd = String.format(cutTs_cmd, tsPath, tsFolder.getPath() + File.separator + FileConstant.M3U8_NAME, tsFolder.getPath());
        ProcessUtil.executeCommand(cutTs_cmd, appConfig.getShowFFmpegLog());
        // 删除 index.ts
        FileUtil.del(tsPath);
    }
}
