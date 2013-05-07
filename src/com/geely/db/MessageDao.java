package com.geely.db;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import com.geely.po.Meeting;
import com.geely.po.Message;
import com.geely.po.Warning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 *
 *
 * <p>Title: 好爸妈客户端</p>
 * <p>Description:频道信息对应的数据库操作类</p>
 * <p>创建日期:2013-1-11</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class MessageDao extends BaseDao<Message> {
    public final static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    @SuppressWarnings("unused")
    private Context context;

    public MessageDao(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 保持会议列表
     * @param array
     * @param userId
     */
    public void inserMeetingArray(JSONArray array, Integer userId) {
        int size = array.length();
        JSONObject jsonMetting = null;
        ContentValues contentValues;
        List<ContentValues> meetingList = new ArrayList<ContentValues>();

        for (int i = 0; i < size; i++) {
            try {
                jsonMetting = array.getJSONObject(i);
                contentValues = new ContentValues();
                contentValues.put("user_id", userId);
                contentValues.put("mtime", jsonMetting.getString("TIME"));
                contentValues.put("address", jsonMetting.getString("ADDRESS"));
                contentValues.put("join_users",
                    jsonMetting.getString("JOIN_USERS"));
                contentValues.put("title", jsonMetting.getString("TITLE"));
                contentValues.put("content", jsonMetting.getString("CONTENT"));
                contentValues.put("send_user",
                    jsonMetting.getString("CRUSER"));
                contentValues.put("require", jsonMetting.getString("REQUIRE"));
                contentValues.put("receive_time", new Date().getTime());
                contentValues.put("meeting_type", "01");
                contentValues.put("read_flag", 0);
                meetingList.add(contentValues);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            readWriteLock.writeLock().lock();
            beginTransaction();

            for (ContentValues values : meetingList) {
                this.insert("meeting", values);
            }

            commit();
        } catch (Exception e) {
            rollback();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 保持预警列表
     * @param array
     * @param userId
     */
    public void inserWarningArray(JSONArray array, Integer userId) {
        int size = array.length();
        JSONObject jsonMetting = null;
        ContentValues contentValues;
        List<ContentValues> warningList = new ArrayList<ContentValues>();

        for (int i = 0; i < size; i++) {
            try {
                jsonMetting = array.getJSONObject(i);
                contentValues = new ContentValues();
                contentValues.put("user_id", userId);
                contentValues.put("mtime", jsonMetting.getString("TIME"));
                contentValues.put("solve_users",
                    jsonMetting.getString("SOLVE_USER"));
                contentValues.put("title", jsonMetting.getString("TITLE"));
                contentValues.put("content", jsonMetting.getString("CONTENT"));
                contentValues.put("send_user",
                    jsonMetting.getString("CRUSER"));
                contentValues.put("explain", jsonMetting.getString("EXPLAIN"));
                contentValues.put("receive_time", new Date().getTime());
                contentValues.put("lb", jsonMetting.getString("LB"));
                contentValues.put("read_flag", 0);
                warningList.add(contentValues);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            readWriteLock.writeLock().lock();
            beginTransaction();

            for (ContentValues values : warningList) {
                this.insert("warning", values);
            }

            commit();
        } catch (Exception e) {
            rollback();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 获取用户的会议列表
     * @param userId 用户ID
     * @param meetingType 会议类型
     * @return
     */
    public List<Meeting> selectMeetingByUserId(Integer userId,
        String meetingType) {
        List<Meeting> list = new ArrayList<Meeting>();
        String sql = "select id,mtime,title,send_user,receive_time,read_flag from meeting where user_id = ? and meeting_type = ? order by read_flag,receive_time desc";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql,
                    new String[] { userId.toString(), meetingType });
        } finally {
            readWriteLock.readLock().unlock();
        }

        Meeting meeting = null;

        while (cursor.moveToNext()) {
            meeting = new Meeting();
            meeting.setId(cursor.getInt(cursor.getColumnIndex("id")));
            meeting.setMtime(cursor.getString(cursor.getColumnIndex("mtime")));
            meeting.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            meeting.setSendUser(cursor.getString(cursor.getColumnIndex(
                        "send_user")));
            meeting.setReceiveTime(cursor.getLong(cursor.getColumnIndex(
                        "receive_time")));
            meeting.setReadFlag(cursor.getInt(cursor.getColumnIndex("read_flag")));
            list.add(meeting);
        }

        cursor.close();

        return list;
    }

    /**
     * 获取用户的预警列表
     * @param userId 用户ID
     * @param lb 预警类别
     * @return
     */
    public List<Warning> selectWarningByUserId(Integer userId, String lb) {
        List<Warning> list = new ArrayList<Warning>();
        String sql = "select id,mtime,title,send_user,receive_time,read_flag from warning where user_id = ? and lb = ? order by read_flag,receive_time desc";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql, new String[] { userId.toString(), lb });
        } finally {
            readWriteLock.readLock().unlock();
        }

        Warning warning = null;

        while (cursor.moveToNext()) {
            warning = new Warning();
            warning.setId(cursor.getInt(cursor.getColumnIndex("id")));
            warning.setMtime(cursor.getString(cursor.getColumnIndex("mtime")));
            warning.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            warning.setSendUser(cursor.getString(cursor.getColumnIndex(
                        "send_user")));
            warning.setReceiveTime(cursor.getLong(cursor.getColumnIndex(
                        "receive_time")));
            warning.setReadFlag(cursor.getInt(cursor.getColumnIndex("read_flag")));
            list.add(warning);
        }

        cursor.close();

        return list;
    }

    /**
     * 根据ID查找会议详细信息
     * @param id 会议ID
     * @return
     */
    public Meeting findMeetingById(Integer id) {
        String sql = "select * from meeting where id = ?";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql, new String[] { id.toString() });
        } finally {
            readWriteLock.readLock().unlock();
        }

        cursor.moveToNext();

        Meeting meeting = new Meeting();
        meeting.setId(cursor.getInt(cursor.getColumnIndex("id")));
        meeting.setMtime(cursor.getString(cursor.getColumnIndex("mtime")));
        meeting.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        meeting.setContent(cursor.getString(cursor.getColumnIndex("content")));
        meeting.setSendUser(cursor.getString(cursor.getColumnIndex("send_user")));
        meeting.setReceiveTime(cursor.getLong(cursor.getColumnIndex(
                    "receive_time")));
        meeting.setJoinUsers(cursor.getString(cursor.getColumnIndex(
                    "join_users")));
        meeting.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        meeting.setRequire(cursor.getString(cursor.getColumnIndex("require")));
        cursor.close();

        return meeting;
    }

    /**
     * 根据ID查找预警详细信息
     * @param id 会议ID
     * @return
     */
    public Warning findWarningById(Integer id) {
        String sql = "select * from warning where id = ?";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql, new String[] { id.toString() });
        } finally {
            readWriteLock.readLock().unlock();
        }

        cursor.moveToFirst();

        Warning warning = new Warning();
        warning.setId(cursor.getInt(cursor.getColumnIndex("id")));
        warning.setMtime(cursor.getString(cursor.getColumnIndex("mtime")));
        warning.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        warning.setContent(cursor.getString(cursor.getColumnIndex("content")));
        warning.setSendUser(cursor.getString(cursor.getColumnIndex("send_user")));
        warning.setReceiveTime(cursor.getLong(cursor.getColumnIndex(
                    "receive_time")));
        warning.setSolveUsers(cursor.getString(cursor.getColumnIndex(
                    "solve_users")));
        warning.setExplain(cursor.getString(cursor.getColumnIndex("explain")));
        cursor.close();

        return warning;
    }

    /**
     * 更新会议的阅读状态
     * @param id 会议ID
     * @return
     */
    public int updateMettingReadFlag(Integer id) {
        ContentValues values = new ContentValues();
        values.put("read_flag", 1);

        return this.update("meeting", values, "id=?",
            new String[] { id.toString() });
    }

    /**
     * 更新预警的阅读状态
     * @param id 预警ID
     * @return
     */
    public int updateWarningReadFlag(Integer id) {
        ContentValues values = new ContentValues();
        values.put("read_flag", 1);

        return this.update("warning", values, "id=?",
            new String[] { id.toString() });
    }

    /**
     * 删除会议
     * @param id 会议ID
     * @return
     */
    public int deleteMettingById(Integer id) {
        return delete("meeting", "id=?", new String[] { id.toString() });
    }

    /**
     * 删除预警
     * @param id 预警ID
     * @return
     */
    public int deleteWarningById(Integer id) {
        return delete("warning", "id=?", new String[] { id.toString() });
    }

    /**
     * 获取未读的会议数目
     * @param userId
     * @param meetingType
     * @return
     */
    public int getUnreadMettingNum(Integer userId) {
        String sql = "select count(id) from meeting where user_id = ? and read_flag=0";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql, new String[] { userId.toString() });
        } finally {
            readWriteLock.readLock().unlock();
        }

        cursor.moveToFirst();

        int num = cursor.getInt(0);
        cursor.close();

        return num;
    }

    /**
     * 获取未读的预警数目
     * @param userId
     * @param meetingType
     * @return
     */
    public int getUnreadWarningNum(Integer userId) {
        String sql = "select count(id) from warning where user_id = ? and read_flag=0";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql, new String[] { userId.toString() });
        } finally {
            readWriteLock.readLock().unlock();
        }

        cursor.moveToFirst();

        int num = cursor.getInt(0);
        cursor.close();

        return num;
    }

    /**
     * 根据会议类型获取未读的会议数目
     * @param userId
     * @param meetingType
     * @return
     */
    public int getUnreadMettingNumByType(Integer userId, String meetingType) {
        String sql = "select count(id) from meeting where user_id = ? and meeting_type = ? and read_flag=0";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql,
                    new String[] { userId.toString(), meetingType });
        } finally {
            readWriteLock.readLock().unlock();
        }

        cursor.moveToFirst();

        int num = cursor.getInt(0);
        cursor.close();

        return num;
    }

    /**
     * 根据预警类型获取未读的预警数目
     * @param userId
     * @param meetingType
     * @return
     */
    public int getUnreadWarningNumByLb(Integer userId, String lb) {
        String sql = "select count(id) from warning where user_id = ? and lb = ? and read_flag=0";
        Cursor cursor = null;

        try {
            readWriteLock.readLock().lock();
            cursor = this.rawQuery(sql, new String[] { userId.toString(), lb });
        } finally {
            readWriteLock.readLock().unlock();
        }

        cursor.moveToFirst();

        int num = cursor.getInt(0);
        cursor.close();

        return num;
    }
}
