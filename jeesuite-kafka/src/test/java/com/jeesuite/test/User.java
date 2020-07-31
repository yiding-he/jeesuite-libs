/**
 *
 */
package com.jeesuite.test;

import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.kafka.message.DefaultMessage;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年11月3日
 */
public class User implements Serializable {

    private int id;

    private int age;

    private String name;

    private String nickName;

    private int gender;

    private String password;

    private String idcard;

    private Date createdAt;

    private Date updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static void main(String[] args) {

        User user = new User();
        user.setId(100);
        user.setName("jack");

        DefaultMessage message = new DefaultMessage(user).consumerAckRequired(true).header("key1", "value1");
        String json = JsonUtils.toJson(message);
        System.out.println(json);

        message = JsonUtils.toObject(json, DefaultMessage.class);
        System.out.println(message);
    }

}
