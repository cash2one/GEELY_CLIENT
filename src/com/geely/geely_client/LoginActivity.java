package com.geely.geely_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.SharedPreferences.Editor;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.geely.data.GetData;

import com.geely.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * <p>Title: 校讯通教师版</p>
 * <p>Description:登录界面</p>
 * <p>创建日期:2011-12-26</p>
 * @author DengJiaLuo
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
    private EditText userName;
    private EditText passWord;
    private ScrollView scrollView;
    private Handler mHandler = new Handler();
    private ImageView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkLogin()) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.layout_login);
        userName = (EditText) findViewById(R.id.username);
        passWord = (EditText) findViewById(R.id.password);
        scrollView = (ScrollView) findViewById(R.id.login_scroll);
        loginButton = (ImageView) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    login();
                }
            });
        userName.setOnClickListener(this);
        passWord.setOnClickListener(this);
    }

    private boolean checkLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean("islogin", false);
    }

    private void login() {
        String textUserName = userName.getText().toString();
        String textPassWord = passWord.getText().toString();

        if (StringUtil.isEmpty(textUserName)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();

            return;
        }

        if (StringUtil.isEmpty(textPassWord)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("keys", new String[] { "userName", "passWord" });
        intent.putExtra("values", new String[] { textUserName, textPassWord });
        intent.putExtra("url", GetData.SERVER_IP + GetData.LOGIN_URL);
        intent.putExtra("msg", "正在登录...");
        startActivityForResult(intent, 0);
    }

    @Override
    public void onClick(final View view) {
        //这里必须要给一个延迟，如果不加延迟则没有效果。我现在还没想明白为什么  
        mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //将ScrollView滚动到底  
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    view.requestFocus();
                }
            }, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            String result = data.getStringExtra("result");

            if (!"".equals(result)) {
                JSONObject object = null;

                try {
                    object = new JSONObject(result);

                    if ("400".equals(object.getString("RESULTCODE"))) {
                        Toast.makeText(this, object.getString("RESULTDESC"),
                            Toast.LENGTH_SHORT).show();
                    } else if ("200".equals(object.getString("RESULTCODE"))) {
                        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                                Context.MODE_PRIVATE);
                        Editor edit = sharedPreferences.edit();
                        edit.putBoolean("islogin", true);
                        edit.putString("userId", object.getString("userId"));
                        edit.putString("userName", object.getString("userName"));
                        edit.putString("tel", object.getString("tel"));
                        edit.putString("proSet", object.getString("proSet"));
                        edit.commit();

                        Intent intent = new Intent();
                        intent.setClass(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "服务器连接异常", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
