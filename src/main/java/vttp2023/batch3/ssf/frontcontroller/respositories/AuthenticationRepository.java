package vttp2023.batch3.ssf.frontcontroller.respositories;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp2023.batch3.ssf.constant.ConstantVar;

@Repository
public class AuthenticationRepository {

	// TODO Task 5
	// Use this class to implement CRUD operations on Redis

	@Autowired
    @Qualifier(ConstantVar.template01)
    RedisTemplate<String,String> template;

    
    // slide24 - create.update a value
    public void createValue(String redisKey,String value) {//set name "Fred Flintstone"
        template.opsForValue().set(redisKey,value); // template.opsForValue().set("name","Fred Flintstone");

        // setifpresent
        // setifabsent
    }

	public void setKeyWithTTL(String key, String value, long seconds) {
        // Set the key with value and TTL
        template.opsForValue().set(key, value, Duration.ofSeconds(seconds));
    }

    public void expireKey(String redisKey, long seconds) {
        Duration expireDuration = Duration.ofSeconds(seconds);
        template.expire(redisKey, expireDuration);
    }

    public void updateValue(String redisKey,String value) {
        template.opsForValue().set(redisKey,value);
    }

    public String getValue(String redisKey) { //get name
        return template.opsForValue().get(redisKey);  //Optional<String> opt = Optional.ofNullable(template.opsForValue().get("name")));
        //if (opt.isPresent()) {String name = opt.get();}

    }
        //slide 27
    public Boolean deleteValue(String redisKey) {//del email
        return template.delete(redisKey);//tempalte.delete("email")

    }
    //26 - only works for key with integer value
    public void incrementValue(String redisKey) { //incr count
        template.opsForValue().increment(redisKey); //template.opsForValue().increment("count");
    }

    public void decrementValue(String redisKey) {
        template.opsForValue().decrement(redisKey);
    }

    public void incrementByValue(String redisKey,Integer value) { //incryby count 10
        template.opsForValue().increment(redisKey,value);//template.opsForValue().increment("count",10);
    }

    public void decrementByValue(String redisKey,Integer value) {
        template.opsForValue().decrement(redisKey,value);
    }

    public boolean checkExists(String redisKey) {
        return template.hasKey(redisKey); //boolean hasEmail = tempalte.hasKey("email");
    }

}
