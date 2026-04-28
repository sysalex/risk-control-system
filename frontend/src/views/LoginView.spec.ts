import { mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

import { ACCESS_TOKEN_KEY } from '@/api/http'

import LoginView from './LoginView.vue'

describe('LoginView', () => {
  it('submits username and password through the login handler', async () => {
    const login = vi.fn().mockResolvedValue(undefined)
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          login,
        },
      },
    })

    await wrapper.get('[data-test="username-input"]').setValue('alice')
    await wrapper.get('[data-test="password-input"]').setValue('password123')
    await wrapper.get('[data-test="login-form"]').trigger('submit')

    expect(login).toHaveBeenCalledWith({
      username: 'alice',
      password: 'password123',
    })
  })

  it('stores tokens after successful login', async () => {
    const login = vi.fn().mockResolvedValue({
      data: {
        data: {
          accessToken: 'access-token',
          refreshToken: 'refresh-token',
        },
      },
    })
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          login,
        },
      },
    })

    await wrapper.get('[data-test="username-input"]').setValue('alice')
    await wrapper.get('[data-test="password-input"]').setValue('password123')
    await wrapper.get('[data-test="login-form"]').trigger('submit')

    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toBe('access-token')
  })

  it('shows error message when login fails', async () => {
    const login = vi.fn().mockRejectedValue(new Error('invalid credentials'))
    const wrapper = mount(LoginView, {
      global: {
        provide: {
          login,
        },
      },
    })

    await wrapper.get('[data-test="login-form"]').trigger('submit')

    expect(wrapper.text()).toContain('登录失败')
  })
})
