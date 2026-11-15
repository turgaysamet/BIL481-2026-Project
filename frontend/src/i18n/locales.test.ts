import { describe, expect, it } from 'vitest';
import en from './locales/en.json';
import es from './locales/es.json';
import tr from './locales/tr.json';

// NFR-06: 100% of the interface texts must exist in Turkish, English and Spanish.
function keys(obj: object, prefix = ''): string[] {
  return Object.entries(obj).flatMap(([key, value]) =>
    typeof value === 'object' && value !== null ? keys(value, `${prefix}${key}.`) : [`${prefix}${key}`],
  );
}

describe('translation files', () => {
  const english = keys(en).sort();

  it.each([['tr', tr], ['es', es]])('%s has exactly the same keys as en', (_name, locale) => {
    expect(keys(locale).sort()).toEqual(english);
  });

  it('has no empty texts', () => {
    for (const locale of [en, tr, es]) {
      expect(JSON.stringify(locale)).not.toContain('""');
    }
  });
});
