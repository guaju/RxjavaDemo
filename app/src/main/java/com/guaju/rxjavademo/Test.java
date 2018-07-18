package com.guaju.rxjavademo;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * Created by guaju on 2018/7/18.
 */

public class Test {

    public static void main(String[] args){
        //1、创建被观察者

        //①、Observable创建的第一种写法 传入OnSubscribe对象
//        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                //依次传递数据给订阅者
//                subscriber.onNext("在吗");
//                subscriber.onNext("在吗，还记得我吗？");
//                subscriber.onNext("。。。");
////                subscriber.onCompleted();
//
//                subscriber.onError(new RuntimeException("呵呵呵呵"));
//            }
//        });
        //②、通过just方法去创建Observable对象
        //简化了方法一的书写，just里面的参数实际上就是方法1中的onNext（）方法中的参数 ，而且just方法 会自动在结尾补全
        //onCompleted方法
//        Observable<String> observable = Observable.just("不好意思加错了", "你的号码和我的一个朋友很像", "你现在从事什么职业", "....", "你喜欢喝茶吗？","绿茶我还是觉得统一的好");
        //③、通过from方法创建Observable对象
        String[] str={"一等奖：macpro 顶配一台","二等奖：qq小轿车使用权一年","三等奖：iphonex","阳光普照奖:德芙巧克力一盒"};
        Observable<String> observable = Observable.from(str);

        //2、创建观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("一切都结束了");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
                System.out.println("---------");
            }
        };

        //3、创建订阅者
        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onCompleted() {
                System.out.println("我们分手吧");
            }

            @Override
            public void onError(Throwable e) {
               
            }

            @Override
            public void onNext(String s) {
                System.out.println("+++"+s+"+++");
            }
        };

        //4、关联起来
//        observable.subscribe(observer);
        observable.subscribe(subscriber);
    }
}
