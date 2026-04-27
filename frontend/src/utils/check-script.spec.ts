import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const rootScript = (name: string) => resolve(process.cwd(), '..', 'scripts', name)

describe('quality gate scripts', () => {
  it('provides a PowerShell quality gate for Windows environments', () => {
    const script = readFileSync(rootScript('check.ps1'), 'utf8')

    expect(script).toContain('pnpm type-check')
    expect(script).toContain('pnpm lint')
    expect(script).toContain('pnpm coverage')
    expect(script).toContain('pnpm build')
    expect(script).toContain('mvn test')
  })

  it('keeps the Bash quality gate aligned with frontend and backend checks', () => {
    const script = readFileSync(rootScript('check.sh'), 'utf8')

    expect(script).toContain('mvn test')
    expect(script).toContain('pnpm type-check')
    expect(script).toContain('pnpm lint')
    expect(script).toContain('pnpm coverage')
    expect(script).toContain('pnpm build')
  })
})
