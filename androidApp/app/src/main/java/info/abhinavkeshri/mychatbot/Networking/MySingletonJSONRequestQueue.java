package info.abhinavkeshri.mychatbot.Networking;



import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

public class MySingletonJSONRequestQueue {
    private static MySingletonJSONRequestQueue instance;
    private ImageLoader imageLoader;
    private static Context ctx;
    RequestQueue requestQueue;
    DiskBasedCache mCache;
    com.android.volley.Network mNetwork;
    private MySingletonJSONRequestQueue(Context context){
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }
    public static synchronized MySingletonJSONRequestQueue getInstance(Context context){
        if(instance == null){
            instance = new MySingletonJSONRequestQueue(context);
        }
        return instance;
    }
    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            mCache = new DiskBasedCache(ctx.getCacheDir(), 4*1024*1024);
            mNetwork = new BasicNetwork(new HurlStack());

            requestQueue = new RequestQueue(mCache, mNetwork);
            requestQueue.start();
        }
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
    public ImageLoader getImageLoader(){
        return imageLoader;
    }
}
