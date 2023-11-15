package com.skyblue.skybluea.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.retrofit.APIClient;
import com.skyblue.skybluea.retrofit.APIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListViewModel extends ViewModel {
    private MutableLiveData<List<Post>> postList;

    public PostListViewModel(){
        postList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Post>> getUserListObserver(){
        return postList;
    }

    public void makeApiCall(){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Post>> call=apiInterface.getCommonPosts("1");
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                postList.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                postList.postValue(null);
                Log.e("Error :",t.getMessage().toString());
            }
        });
    }
}