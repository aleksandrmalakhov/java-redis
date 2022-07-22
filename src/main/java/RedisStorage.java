import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisStorage {
    private final Jedis jedis;
    private List<Tuple> result;
    private Set<String> purchases;

    public RedisStorage(Jedis jedis) {
        this.jedis = jedis;
    }

    public void init() throws InterruptedException {
        jedis.del(Const.KEY_USER);

        for (int i = 1; i <= 20; i++) {
            jedis.zadd(Const.KEY_USER, new Date().getTime(), String.valueOf(i));
            Thread.sleep(30);
        }
        result = jedis.zrangeWithScores(Const.KEY_USER, 0, -1);

        print();
    }

    private void print() throws InterruptedException {
        purchases = new HashSet<>();
        for (Tuple tuple : result) {
            String showedUser = tuple.getElement();
            if (!purchases.contains(showedUser)) {
                System.out.printf("— На главной странице показываем пользователя %s%n", showedUser);
            }
            if (Math.random() < 0.10) {
                buy();
            }
        }
        Thread.sleep(1000);
        init();
    }

    private void buy() {
        int i = (int) (result.size() * Math.random());
        String element = result.get(i).getElement();
        purchases.add(element);
        System.out.printf(">Пользователь %s оплатил платную услугу%n", element);
        System.out.printf("— На главной странице показываем пользователя %s%n", element);
    }
}