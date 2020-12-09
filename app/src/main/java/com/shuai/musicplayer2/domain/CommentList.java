package com.shuai.musicplayer2.domain;

import java.util.List;

public class CommentList {

    @Override
    public String toString() {
        return "CommentList{" +
                "hotComments=" + hotComments +
                '}';
    }

    private java.util.List<HotCommentsBean> hotComments;

    public List<HotCommentsBean> getHotComments() {
        return hotComments;
    }

    public void setHotComments(List<HotCommentsBean> hotComments) {
        this.hotComments = hotComments;
    }

    public static class HotCommentsBean {
        @Override
        public String toString() {
            return "HotCommentsBean{" +
                    "user=" + user +
                    ", content='" + content + '\'' +
                    '}';
        }



        private UserBean user;
        private String content;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

        public static class UserBean {

            private String nickname;
            private String avatarUrl;

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }
        }
}
