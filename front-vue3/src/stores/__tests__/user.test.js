import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Mock localStorage
const localStorageMock = (() => {
  let store = {}
  return {
    getItem: vi.fn((key) => store[key] || null),
    setItem: vi.fn((key, value) => { store[key] = value }),
    removeItem: vi.fn((key) => { delete store[key] }),
    clear: vi.fn(() => { store = {} }),
  }
})()
Object.defineProperty(globalThis, 'localStorage', { value: localStorageMock })

// Mock request
vi.mock('@/utils/request', () => ({
  default: vi.fn(),
}))

import { useUserStore } from '@/stores/user'

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('should initialize with default state', () => {
    const store = useUserStore()
    expect(store.token).toBe('')
    expect(store.userTable).toBe('')
    expect(store.isLoggedIn).toBe(false)
  })

  it('should set token correctly', () => {
    const store = useUserStore()
    store.setToken('test-token-123')
    expect(store.token).toBe('test-token-123')
    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'test-token-123')
  })

  it('should set user table correctly', () => {
    const store = useUserStore()
    store.setUserTable('yonghu')
    expect(store.userTable).toBe('yonghu')
    expect(localStorageMock.setItem).toHaveBeenCalledWith('userTable', 'yonghu')
  })

  it('should be logged in when token and userTable are set', () => {
    const store = useUserStore()
    store.setToken('abc')
    store.setUserTable('yonghu')
    expect(store.isLoggedIn).toBe(true)
  })

  it('should clear all state on logout', () => {
    const store = useUserStore()
    store.setToken('abc')
    store.setUserTable('yonghu')
    store.logout()
    expect(store.token).toBe('')
    expect(store.userTable).toBe('')
    expect(store.isLoggedIn).toBe(false)
  })
})
