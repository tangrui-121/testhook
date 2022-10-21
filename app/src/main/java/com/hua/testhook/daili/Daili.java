package com.hua.testhook.daili;

import java.util.Date;

// 定义接口
public class Daili {

    public interface UserService {
        public void select();
        public void update();
        public void update1();
    }

    public static class UserServiceImpl implements UserService {
        public void select() {
            System.out.println("查询 selectById");
        }

        public void update() {
            System.out.println("更新 update");
        }

        public void update1() {
            System.out.println("更新 update1");
        }
    }


    // 静态代理
    public static class UserServiceProxy implements UserService {
        private UserService target; // 被代理的对象

        public UserServiceProxy(UserService target) {
            this.target = target;
        }
        public void select() {
            before();
            target.select();    // 这里才实际调用真实主题角色的方法
            after();
        }
        public void update() {
            before();
            target.update();    // 这里才实际调用真实主题角色的方法
            after();
        }

        @Override
        public void update1() {
            before();
            target.update1();    // 这里才实际调用真实主题角色的方法
            after();
        }

        private void before() {     // 在执行方法之前执行
            System.out.println(String.format("log start time [%s] ", new Date()));
        }
        private void after() {      // 在执行方法之后执行
            System.out.println(String.format("log end time [%s] ", new Date()));
        }
    }

    public static void main(String[] args) {
        UserService userServiceImpl = new UserServiceImpl();
        UserService proxy = new UserServiceProxy(userServiceImpl);
        proxy.select();
        proxy.update();
        proxy.update1();
    }
}