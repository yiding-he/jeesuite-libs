/**
 *
 */
package test.serlalize;

import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.common.serializer.JavaSerializer;
import com.jeesuite.common.serializer.SerializeUtils;
import test.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月28日
 */
public class SerliazePerfTest {

    public static void main(String[] args) throws IOException {
        JavaSerializer javaSerializer = new JavaSerializer();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(i + 1000, "user" + i));
        }

        byte[] bytes = javaSerializer.serialize(users);
        System.out.println(bytes.length);
        System.out.println(SerializeUtils.serialize(users).length);
        System.out.println(JsonUtils.toJson(users).getBytes().length);


    }
}
