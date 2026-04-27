package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.user.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link UserMapper} 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class UserMapperTest {

    /**
     * 验证 Mapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void extendsBaseMapperForUserEntity() {
        Type genericInterface = UserMapper.class.getGenericInterfaces()[0];

        assertTrue(BaseMapper.class.isAssignableFrom(UserMapper.class));
        assertInstanceOf(ParameterizedType.class, genericInterface);
        assertEquals(User.class, ((ParameterizedType) genericInterface).getActualTypeArguments()[0]);
    }
}
