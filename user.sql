create table user
(
    id              int auto_increment comment '自增id不解释'
        primary key,
    username        varchar(32)          null comment '玩家的用户名',
    uuid            varchar(128)         null comment '玩家的java uuid',
    password        varchar(128)         null comment '密码，使用sha-1储存',
    regTime         int                  null comment '注册时间',
    onlineTime      int                  null comment '共计在服务器游玩的时间',
    lastLoginServer int                  null comment '最后一次在服务器上线的时间',
    lastLoginWeb    int                  null comment '最后一次在服务器官网上线的时间',
    money           int        default 0 null comment '账户余额',
    isAccepted      tinyint(1) default 0 null comment '是否通过答题',
    constraint user_username_uindex
        unique (username),
    constraint user_uuid_uindex
        unique (uuid)
)
    comment '用于服务器用户';

