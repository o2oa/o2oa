<template>
  <div v-if="loadingStore.isLoading" class="loading-overlay">
    <div class="loading-spinner"></div>
    <slot name="text">{{ loadingStore.loadingText }}</slot>
  </div>
</template>

<script>
import {useLoadingStore} from '../store.js';

export default {
  name: "Loading",
  setup() {
    const loadingStore = useLoadingStore();
    return {
      loadingStore
    }
  }
};
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  color: #fff;
  gap: 10px;
}

.loading-spinner {
  border: 5px solid rgba(255, 255, 255, 0.5);
  border-top: 5px solid #fff;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>