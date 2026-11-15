<!-- Vue 3 version of the same card, for comparison. -->
<script setup lang="ts">
import { useI18n } from 'vue-i18n';

interface Suggestion {
  profile: { userId: number; displayName: string; interests: { name: string }[] };
  score: { total: number };
}

const props = defineProps<{ suggestion: Suggestion; requested: boolean }>();
const emit = defineEmits<{ (e: 'connect', userId: number): void }>();
const { t } = useI18n();
</script>

<template>
  <article class="card match">
    <h3>{{ props.suggestion.profile.displayName }}</h3>
    <p class="score">{{ t('matches.compatibility') }}: <strong>{{ props.suggestion.score.total }}%</strong></p>
    <p>{{ props.suggestion.profile.interests.map((i) => i.name).join(' · ') }}</p>
    <button :disabled="props.requested" @click="emit('connect', props.suggestion.profile.userId)">
      {{ props.requested ? t('matches.requested') : t('matches.connect') }}
    </button>
  </article>
</template>
<!-- Template syntax (v-if, :prop, @event) is easy to learn; type checking of templates needs extra tooling (vue-tsc). -->
