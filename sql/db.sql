create database live;

use live;

create table user
(
    id                 varchar(10) primary key comment '用户 id',
    nickName           varchar(20)                            not null comment '昵称',
    avatar             varchar(100)                           null comment '头像',
    email              varchar(150)                           not null comment '邮箱',
    password           varchar(50)                            not null comment '密码',
    sex                tinyint(1) comment '0:女 1:男 2:未知',
    birthday           varchar(10) comment '出生日期',
    school             varchar(150) comment '学校',
    personIntroduction varchar(120) comment '个人简介',
    lastLoginTime      datetime comment '最后登录时间',
    lastLoginIp        varchar(20) comment '最后登录 IP',
    userRole           varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    noticeInfo         varchar(300) comment '空间公告',
    totalCoinCount     int                                    not null comment '硬币总数量',
    currentCoinCount   int                                    not null comment '当前硬币总数量',
    theme              tinyint(1)                             not null default 1 comment '主题',
    createTime         datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime         datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete           tinyint(1)   default 0                 not null comment '是否删除',
    unique index idx_key_email (email),
    unique index idx_nick_name (nickName)
) comment '用户表';

create table category
(
    id               int primary key auto_increment not null comment '分类 id',
    code             varchar(30)                    not null comment '分类编码',
    name             varchar(30)                    not null comment '分类名称',
    parentCategoryId int                            not null comment '父级分类 id',
    icon             varchar(50) default null comment '图标',
    background       varchar(50) default null comment '背景图',
    sort             tinyint(4)                     not null comment '排序号',
    unique index idx_key_code (code) using BTREE
) comment '分类表';

create table videoPost
(
    id               varchar(10) primary key                 not null comment '视频 id',
    cover            varchar(50)                             not null comment '视频封面',
    name             varchar(100)                            not null comment '视频名称',
    userId           varchar(10)                             not null comment '用户 id',
    categoryId       int           default null comment '分类 id',
    parentCategoryId int                                     not null comment '父级分类 id',
    status           tinyint(1)                              not null comment '0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败',
    postType         tinyint(4)                              not null comment '0:自己制作 1:转载',
    originInfo       varchar(200)  default null comment '源资源说明',
    tags             varchar(300)  default null comment '标签',
    introduction     varchar(2000) default null comment '简介',
    interaction      varchar(5)    default null comment '互动设置',
    duration         int           default null comment '持续时间(秒)',
    createTime       datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_create_time (createTime),
    index idx_user_id (userId),
    index idx_category_id (categoryId),
    index idx_parent_category_id (parentCategoryId)
) comment '已发布视频信息表';

create table videoFilePost
(
    fileId         varchar(20) primary key not null comment '文件 id',
    uploadId       varchar(15)             not null comment '上传 id',
    userId         varchar(10)             not null comment '用户 id',
    videoId        varchar(10)             not null comment '视频 id',
    fileIndex      int                     not null comment '文件索引',
    fileName       varchar(200)            not null comment '文件名',
    fileSize       bigint       default null comment '文件大小',
    filePath       varchar(100) default null comment '文件路径',
    updateType     tinyint(4)   default null comment '0:无更新 1:有更新',
    transferResult tinyint(4)   default null comment '0:转码中 1:转码成功 2:转码失败',
    duration       int          default null comment '持续时间(秒)',
    unique index idx_key_upload_id (uploadId, userId),
    index idx_video_id (videoId)
) comment '已发布视频文件信息表';

create table video
(
    id               varchar(10) primary key                 not null comment '视频 id',
    cover            varchar(50)                             not null comment '视频封面',
    name             varchar(100)                            not null comment '视频名称',
    userId           varchar(10)                             not null comment '用户 id',
    categoryId       int           default null comment '分类 id',
    parentCategoryId int                                     not null comment '父级分类 id',
    status           tinyint(1)                              not null comment '0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败',
    postType         tinyint(4)                              not null comment '0:自己制作 1:转载',
    originInfo       varchar(200)  default null comment '源资源说明',
    tags             varchar(300)  default null comment '标签',
    introduction     varchar(2000) default null comment '简介',
    interaction      varchar(5)    default null comment '互动设置',
    duration         int           default null comment '持续时间(秒)',
    playCount        int           default 0 comment '播放数量',
    likeCount        int           default 0 comment '点赞数量',
    danmuCount       int           default 0 comment '弹幕数量',
    commentCount     int           default 0 comment '评论数量',
    coinCount        int           default 0 comment '投币数量',
    collectCount     int           default 0 comment '收藏数量',
    recommendType    tinyint(1)    default 0 comment '是否推荐 0:未推荐 1:已推荐',
    lastPlayTime     datetime      default null comment '最后播放时间',
    createTime       datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_create_time (createTime),
    index idx_user_id (userId),
    index idx_category_id (categoryId),
    index idx_parent_category_id (parentCategoryId),
    index idx_recommend_type (recommendType),
    index idx_last_update_time (lastPlayTime)
) comment '视频信息表';

create table videoFile
(
    fileId    varchar(20) primary key not null comment '文件 id',
    userId    varchar(10)             not null comment '用户 id',
    videoId   varchar(10)             not null comment '视频 id',
    fileIndex int                     not null comment '文件索引',
    fileName  varchar(200)            not null comment '文件名',
    fileSize  bigint       default null comment '文件大小',
    filePath  varchar(100) default null comment '文件路径',
    duration  int          default null comment '持续时间(秒)',
    index idx_video_id (videoId)
) comment '视频文件信息表';

create table action
(
    id          int primary key auto_increment comment 'id',
    videoId     varchar(10)                        not null comment '视频 id',
    videoUserId varchar(10)                        not null comment '视频用户 id',
    commentId   int                                not null comment '评论 id',
    actionType  tinyint(1)                         not null comment '0: 评论喜欢点赞 1:讨厌评论 2:视频点赞 3:视频收藏 4:视频投币',
    count       int                                not null comment '数量',
    userId      varchar(10)                        not null comment '用户 id',
    actionTime  dateTime default CURRENT_TIMESTAMP not null comment '操作时间',
    unique key idx_key_video_comment_type_user (videoId, commentId, actionType, userId),
    key idx_video_id (videoId),
    key idx_user_id (userId),
    key idx_type (actionType),
    key idx_action_time (actionTime)
) comment '用户行为 点赞、评论';

create table danmu
(
    id       int primary key auto_increment comment 'id',
    videoId  varchar(10)                            not null comment '视频 id',
    fileId   varchar(20)                            not null comment '视频文件 id',
    userId   varchar(15)                            not null comment '用户 id',
    postTime datetime     default CURRENT_TIMESTAMP not null comment '发送时间',
    text     varchar(300) default null comment '内容',
    mode     tinyint(1)   default null comment '弹幕位置',
    color    varchar(10)  default null comment '颜色',
    time     int          default null comment '展示时间',
    key idx_file_id (fileId)
) comment '弹幕表';

create table videoComment
(
    id              int primary key auto_increment comment 'id',
    parentCommentId int          not null comment '父级评论 id',
    videoId         varchar(10)  not null comment '视频 id',
    videoUserId     varchar(10)  not null comment '视频用户 id',
    content         varchar(500) not null comment '回复内容',
    imgPath         varchar(150) default null comment '图片内容',
    userId          varchar(15)  not null comment '用户 id',
    replyUserId     varchar(15)  default null comment '回复人 id',
    topType         tinyint(4)   default 0 comment '0: 未置顶 1:已置顶',
    postTime        datetime     not null comment '发布时间',
    likeCount       int          default 0 comment '喜欢数量',
    hateCount       int          default 0 comment '讨厌数量',
    key idx_post_time (postTime),
    key idx_top (topType),
    key idx_p_id (parentCommentId),
    key idx_user_id (userId),
    key idx_video_id (videoId)
) comment '评论表';

create table focus
(
    userId      varchar(10) not null comment '用户 id',
    focusUserId varchar(10) not null comment '用户 id',
    focusTime   datetime default null comment '关注时间',
    primary key (userId, focusUserId) using BTREE
) comment '关注表';

create table series
(
    id          int primary key auto_increment comment '合集 id',
    name        varchar(100) not null comment '合集名称',
    description varchar(200) default null comment '合集描述',
    userId      varchar(10)  not null comment '用户 id',
    sort        tinyint(4)   not null comment '排序',
    updateTime  datetime     default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    key idx_user_id (userId)
) comment '视频合集表';

create table seriesContent
(
    seriesId int         not null comment '合集 id',
    videoId  varchar(10) not null comment '视频 id',
    userId   varchar(10) not null comment '用户 id',
    sort     tinyint(4)  not null comment '排序',
    primary key (seriesId, videoId) using BTREE
) comment '视频合集内容表';

create table message
(
    id         int auto_increment primary key comment '消息 id',
    userId     varchar(10) not null comment '用户 id',
    videoId    varchar(10) not null comment '视频 id',
    type       tinyint(1)           default null comment '消息类型',
    sendUserId varchar(10) not null comment '发生人 id',
    readType   tinyint(1)           default null comment '0:未读 1:已读',
    createTime datetime    not null default CURRENT_TIMESTAMP comment '创建时间',
    extendJson text comment '扩展消息',
    key idx_user_id (userId) using BTREE,
    key idx_read_type (readType) using BTREE,
    key idx_message_type (type) using BTREE
) comment '用户消息表';

create table videoPlayHistory
(
    userId         varchar(10) not null comment '用户 id',
    videoId        varchar(10) not null comment '视频 id',
    fileIndex      int         not null comment '视频文件索引',
    lastUpdateTime datetime    not null comment '最后更新时间',
    primary key (userId(4), videoId) using BTREE,
    key idx_video_id (videoId) using BTREE,
    key idx_user_id (userId) using BTREE
) comment '视频播放历史表';

create table statisticInfo
(
    statisticDate varchar(10) not null comment '统计日期',
    userId        varchar(10) not null comment '用户 id',
    dataType      tinyint(1)  not null comment '数据统计类型',
    count         int default null comment '统计数量'
) comment '数据统计表';