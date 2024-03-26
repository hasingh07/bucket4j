package io.github.bucket4j.redis.jedis;

import java.util.Objects;


import io.github.bucket4j.distributed.proxy.AbstractProxyManagerBuilder;
import io.github.bucket4j.distributed.serialization.Mapper;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.util.Pool;

/**
 * Entry point for Jedis integration
 */
public class Bucket4jJedis {

    /**
     * Returns the builder for {@link JedisBasedProxyManager}
     *
     * @param jedisPool
     *
     * @return new instance of {@link JedisBasedProxyManagerBuilder}
     */
    public static JedisBasedProxyManagerBuilder<byte[]> builderFor(Pool<Jedis> jedisPool) {
        Objects.requireNonNull(jedisPool);
        RedisApi redisApi = new RedisApi() {
            @Override
            public Object eval(byte[] script, int keyCount, byte[]... params) {
                try (Jedis jedis = jedisPool.getResource()) {
                    return jedis.eval(script, 1, params);
                }
            }
            @Override
            public byte[] get(byte[] key) {
                try (Jedis jedis = jedisPool.getResource()) {
                    return jedis.get(key);
                }
            }
            @Override
            public void delete(byte[] key) {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.del(key);
                }
            }
        };
        return new JedisBasedProxyManagerBuilder<>(Mapper.BYTES, redisApi);
    }

    /**
     * Returns the builder for {@link JedisBasedProxyManager}
     *
     * @param unifiedJedis
     *
     * @return new instance of {@link JedisBasedProxyManagerBuilder}
     */
    public static JedisBasedProxyManagerBuilder<byte[]> builderFor(UnifiedJedis unifiedJedis) {
        Objects.requireNonNull(unifiedJedis);
        RedisApi redisApi = new RedisApi() {
            @Override
            public Object eval(byte[] script, int keyCount, byte[]... params) {
                return unifiedJedis.eval(script, keyCount, params);
            }

            @Override
            public byte[] get(byte[] key) {
                return unifiedJedis.get(key);
            }

            @Override
            public void delete(byte[] key) {
                unifiedJedis.del(key);
            }
        };
        return new JedisBasedProxyManagerBuilder<>(Mapper.BYTES, redisApi);

    }

    /**
     * Returns the builder for {@link JedisBasedProxyManager}
     *
     * @param jedisCluster
     *
     * @return new instance of {@link JedisBasedProxyManagerBuilder}
     */
    public static JedisBasedProxyManagerBuilder<byte[]> builderFor(JedisCluster jedisCluster) {
        Objects.requireNonNull(jedisCluster);
        RedisApi redisApi = new RedisApi() {
            @Override
            public Object eval(byte[] script, int keyCount, byte[]... params) {
                return jedisCluster.eval(script, keyCount, params);
            }
            @Override
            public byte[] get(byte[] key) {
                return jedisCluster.get(key);
            }
            @Override
            public void delete(byte[] key) {
                jedisCluster.del(key);
            }
        };
        return new JedisBasedProxyManagerBuilder<>(Mapper.BYTES, redisApi);
    }

    public static class JedisBasedProxyManagerBuilder<K> extends AbstractProxyManagerBuilder<K, JedisBasedProxyManager<K>, JedisBasedProxyManagerBuilder<K>> {

        final RedisApi redisApi;
        Mapper<K> keyMapper;

        public JedisBasedProxyManagerBuilder(Mapper<K> keyMapper, RedisApi redisApi) {
            this.redisApi = redisApi;
            this.keyMapper = Objects.requireNonNull(keyMapper);
        }

        @Override
        public JedisBasedProxyManager<K> build() {
            return new JedisBasedProxyManager<>(this);
        }

        /**
         * Specifies the type of key.
         *
         * @param keyMapper object responsible for converting primary keys to byte arrays.
         *
         * @return this builder instance
         */
        public <K2> JedisBasedProxyManagerBuilder<K2> keyMapper(Mapper<K2> keyMapper) {
            this.keyMapper = (Mapper) Objects.requireNonNull(keyMapper);
            return (JedisBasedProxyManagerBuilder<K2>) this;
        }

        public Mapper<K> getKeyMapper() {
            return keyMapper;
        }

        public RedisApi getRedisApi() {
            return redisApi;
        }
    }

}
