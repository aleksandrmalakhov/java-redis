import redis.clients.jedis.Jedis;

public class Main {
    private static Jedis jedis;

    public static void main(String[] args) {
        try {
            jedis = new Jedis(Const.URL);
            RedisStorage redis = new RedisStorage(jedis);
            redis.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
