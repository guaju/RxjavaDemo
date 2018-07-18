package com.guaju.rxjavademo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView, imageView2;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.iv);
        imageView2 = (ImageView) findViewById(R.id.iv2);

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
        observable
                .subscribeOn(Schedulers.io())// OnSubscribe所在的线程
                .observeOn(AndroidSchedulers.mainThread()) //控制的是订阅者更新时所在线程
                .subscribe(subscriber);
    }
}
