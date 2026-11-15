import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './locales/en.json';
import es from './locales/es.json';
import tr from './locales/tr.json';

export const SUPPORTED_LANGUAGES = ['tr', 'en', 'es'] as const;
const LANGUAGE_KEY = 'campusbridge.language';

function savedLanguage(): string {
  try {
    return localStorage.getItem(LANGUAGE_KEY) ?? 'en';
  } catch {
    return 'en';
  }
}

// FR-14 / NFR-06: every interface text comes from these three resource files.
i18n.use(initReactI18next).init({
  resources: { en: { translation: en }, tr: { translation: tr }, es: { translation: es } },
  lng: savedLanguage(),
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

export function changeLanguage(language: string): void {
  i18n.changeLanguage(language);
  try {
    localStorage.setItem(LANGUAGE_KEY, language);
  } catch {
    /* ignore */
  }
}

export default i18n;
