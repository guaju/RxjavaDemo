package com.guaju.rxjavademo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView, imageView2;
    int count = 0;


    @SuppressLint("HandlerLeak")
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            if (what==0){
                String obj = (String) msg.obj;

            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.iv);
        imageView2 = (ImageView) findViewById(R.id.iv2);


        new Thread(new Runnable(){

            @Override
            public void run() {
                //处理数据
                String  str="";
                Message obtain = Message.obtain();
                obtain.what=0;
                obtain.obj=str;
                mHandler.sendMessage(obtain);

            }
        });

//        RxJava 1.0的版本：
//        Observable  被观察者
//        Observer    观察者
//        Subscriber  订阅者
//        Subscription 描述被观察者和观察者之间关系的一个类
//        OnSubscribe  当订阅者绑定到被观察者时要做的事的一个类

//        //1、创建被观察者
//        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                //依次传递数据给订阅者
//                subscriber.onNext("在吗");
//                subscriber.onNext("在吗，还记得我吗？");
//                subscriber.onNext("。。。");
//            }
//        });
//        //2、创建观察者
//        Observer<String> observer = new Observer<String>() {
//            @Override
//            public void onCompleted() {
//                System.out.println("一切都结束了");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println(e.getMessage());
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.println(s+"---");
//            }
//        };
//
//        //3、创建订阅者
//        Subscriber<String> subscriber = new Subscriber<String>() {
//
//            @Override
//            public void onCompleted() {
//                System.out.println("我们分手吧");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.println("+++"+s+"+++");
//            }
//        };
//
//        //4、关联起来
//        observable.subscribe(observer);
////        observable.subscribe(subscriber);

//        rxjava最大的作用其实是调度线程  替代了 handler   asynctask  runOnUiThread ，他还有监听的作用


        /**
         *  httpurlconnection进行图片获取,并且展示
         */
        final String path = "https://www.baidu.com/img/bd_logo1.png";
        final String path1 = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=4034785302,4142975295&fm=173&app=25&f=JPEG?w=598&h=619&s=0B10CB0215A6B6BAA00C20BA0300A010";
        //第一步创建一个返回值为bitmapdrawable的 被观察者

        Observable<BitmapDrawable> observable = Observable.create(new Observable.OnSubscribe<BitmapDrawable>() {
            private BitmapDrawable bitmapDrawable, bitmapDrawable2;
            private InputStream inputStream, inputStream2;

            @Override
            public void call(Subscriber<? super BitmapDrawable> subscriber) {
                //联网获取图片  （属于耗时操作 将来应该放在子线程）
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmapDrawable = new BitmapDrawable(bitmap);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //将处理之后的值传给了subscriber
                subscriber.onNext(bitmapDrawable);
                SystemClock.sleep(2000);
                try {
                    URL url = new URL(path1);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    inputStream2 = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream2);
                    bitmapDrawable2 = new BitmapDrawable(bitmap);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                subscriber.onNext(bitmapDrawable2);


            }
        });

        //创建订阅者
        Subscriber subscriber = new Subscriber<BitmapDrawable>() {

            @Override
            public void onStart() {
                System.out.println("我要开始啦~~~");


            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(BitmapDrawable bitmapDrawable) {
                //这其实是做真正的ui变化 （放在主线程）
                if (count == 0)
                    imageView.setBackgroundDrawable(bitmapDrawable);
                if (count == 1)
                    imageView2.setBackgroundDrawable(bitmapDrawable);
                count++;
            }
        };
        Subscription subscribe = observable
                .observeOn(AndroidSchedulers.mainThread()) //控制的是订阅者更新时所在线程
                .subscribeOn(Schedulers.io())// OnSubscribe所在的线程
                .subscribe(subscriber);

    }

//    高级问题？
//    subscribeOn
//    observeOn
//            分别能够调用几次？
//
//                subscribeOn方法只能调用1次
//                 observeOn方法会调用多次
//
//
//
//
//
//    //Observable  被观察者
////    create方法 ：创建Observable对象 ，并且 将OnSubscribe变量指定 call（）中有操作订阅者subscriber  onNext的逻辑
////    subscribe方法：是先将订阅者Subscriber转成SafeSubscriber 然后 去调用  OnSubscribe的call方法 去申请"通知"
//
//
//    //Observer    观察者
////        接口：里面有三个方法
////                onNext    更新
////                onCompleted     更新结束
////                onError         更新失败时
//
//
//    //Subscriber  订阅者
//       1、实现了Observer接口和Subscription接口 但是只实现了  Subscription接口的方法
//            Observer接口的方法没有实现
//       2、提供了onstart方法 这个方法会先于onNext方法执行，所以如果有初始化的操作，可以重写onStart中去使用
//       3、内部有一个Subscriptionlist这样的类，他是用来记录所有订阅者和被观察着关系的类
//
//
//    //OnSubscribe 被观察者 通知 观察者时做的操作  类似于观察者模式中的notify方法  能够管理通知的操作
//      是一个接口，继承Action1的方法 里面有call()方法
//
//
//
//    //Subscription  描述观察者和被观察者关系的一个类
////            接口：俩方法
////                unSubscribe 取消订阅
////                isUnSubscribed   判断是否取消订阅
//
//
//
//
//
//
//

}
