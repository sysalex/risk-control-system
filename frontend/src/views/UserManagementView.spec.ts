import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

import UserManagementView from './UserManagementView.vue'

describe('UserManagementView', () => {
  it('loads and renders users', async () => {
    const users = [
      { id: 1, username: 'alice', email: 'alice@example.com', role: 'admin', enabled: true },
      { id: 2, username: 'bob', email: 'bob@example.com', role: 'analyst', enabled: false },
    ]
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: users } }),
      create: vi.fn(),
      update: vi.fn(),
      remove: vi.fn(),
    }

    const wrapper = mount(UserManagementView, {
      global: {
        provide: {
          userApi: api,
        },
      },
    })
    await flushPromises()

    expect(api.list).toHaveBeenCalledWith({ page: 1, limit: 20 })
    expect(wrapper.get('[data-test="user-table"]').text()).toContain('alice')
    expect(wrapper.get('[data-test="user-table"]').text()).toContain('bob@example.com')
  })

  it('creates user and reloads list', async () => {
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: [] } }),
      create: vi.fn().mockResolvedValue({}),
      update: vi.fn(),
      remove: vi.fn(),
    }
    const wrapper = mount(UserManagementView, {
      global: {
        provide: {
          userApi: api,
        },
      },
    })
    await flushPromises()

    await wrapper.get('[data-test="username-input"]').setValue('carol')
    await wrapper.get('[data-test="email-input"]').setValue('carol@example.com')
    await wrapper.get('[data-test="password-input"]').setValue('password123')
    await wrapper.get('[data-test="user-form"]').trigger('submit')
    await flushPromises()

    expect(api.create).toHaveBeenCalledWith({
      username: 'carol',
      email: 'carol@example.com',
      password: 'password123',
      role: 'analyst',
    })
    expect(api.list).toHaveBeenCalledTimes(2)
  })
})
