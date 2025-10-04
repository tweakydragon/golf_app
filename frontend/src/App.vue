<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const isMenuOpen = ref(false);
const isPinned = ref(false);
let hoverTimer = null;

const openMenu = () => {
  isMenuOpen.value = true;
};

const toggleMenu = () => {
  if (isMenuOpen.value) {
    closeMenu(true);
  } else {
    openMenu();
  }
};

const closeMenu = (force = false) => {
  if (force || !isPinned.value) {
    isMenuOpen.value = false;
  }
};

const clearHoverTimer = () => {
  if (hoverTimer) {
    clearTimeout(hoverTimer);
    hoverTimer = null;
  }
};

const scheduleClose = () => {
  if (!isPinned.value && isMenuOpen.value) {
    clearHoverTimer();
    hoverTimer = setTimeout(() => {
      isMenuOpen.value = false;
      hoverTimer = null;
    }, 250);
  }
};

const onMenuHover = () => {
  clearHoverTimer();
  openMenu();
};

const onMenuLeave = () => {
  scheduleClose();
};

const togglePin = () => {
  isPinned.value = !isPinned.value;
  if (!isPinned.value) {
    scheduleClose();
  }
};

const handleEsc = (event) => {
  if (event.key === 'Escape' && isMenuOpen.value && !isPinned.value) {
    closeMenu(true);
  }
};

const handleFocusIn = () => {
  onMenuHover();
};

const handleFocusOut = (event) => {
  if (isPinned.value) {
    return;
  }
  const menu = event.currentTarget;
  const related = event.relatedTarget;
  if (!menu.contains(related)) {
    scheduleClose();
  }
};

onMounted(() => {
  document.addEventListener('keydown', handleEsc);
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleEsc);
  clearHoverTimer();
});
</script>

<template>
  <div id="app-container">
    <button
      type="button"
      class="menu-toggle"
      @click="toggleMenu"
      :aria-expanded="isMenuOpen"
      aria-controls="side-menu"
      aria-label="Toggle navigation"
    >
      <span class="menu-toggle__line"></span>
      <span class="menu-toggle__line"></span>
      <span class="menu-toggle__line"></span>
    </button>

    <div
      v-if="isMenuOpen && !isPinned"
      class="side-menu-backdrop"
      @click="closeMenu(true)"
      aria-hidden="true"
    ></div>

    <nav
      id="side-menu"
      class="side-menu"
      :class="{ open: isMenuOpen, pinned: isPinned }"
      @mouseenter="onMenuHover"
      @mouseleave="onMenuLeave"
      @focusin="handleFocusIn"
      @focusout="handleFocusOut"
    >
      <div class="side-menu__header">
        <h5 class="side-menu__title">Menu</h5>
        <div class="side-menu__actions">
          <button
            type="button"
            class="icon-button"
            @click="togglePin"
            :aria-pressed="isPinned"
            aria-label="Pin menu"
          >
            <i :class="['bi', isPinned ? 'bi-pin-angle-fill' : 'bi-pin-angle']"></i>
          </button>
          <button
            type="button"
            class="icon-button"
            @click="closeMenu(true)"
            aria-label="Close menu"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
      </div>
      <div class="side-menu__content">
        <ul class="menu-items">
          <li>
            <router-link to="/" class="nav-link" @click="closeMenu(true)">Home</router-link>
          </li>
          <li>
            <router-link to="/upload" class="nav-link" @click="closeMenu(true)">Upload CSV</router-link>
          </li>
        </ul>
      </div>
    </nav>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style>
#app-container {
  min-height: 100vh;
}

.menu-toggle {
  position: fixed;
  top: 20px;
  left: 20px;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.12);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  z-index: 1100;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.menu-toggle:hover,
.menu-toggle:focus-visible {
  transform: translateY(-1px);
  box-shadow: 0 14px 35px rgba(15, 23, 42, 0.18);
  outline: none;
}

.menu-toggle__line {
  width: 24px;
  height: 2px;
  background-color: #212529;
  border-radius: 2px;
}

.side-menu-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  backdrop-filter: blur(2px);
  z-index: 1040;
}

.side-menu {
  position: fixed;
  top: 0;
  left: 0;
  width: 280px;
  max-width: 90vw;
  height: 100vh;
  background-color: var(--color-surface);
  border-right: 1px solid var(--color-border);
  box-shadow: 0 0 40px rgba(15, 23, 42, 0.16);
  padding: 20px 20px 32px;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
  z-index: 1050;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.side-menu.open {
  transform: translateX(0);
}

.side-menu.pinned {
  box-shadow: none;
}

.side-menu__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.side-menu__title {
  margin: 0;
  font-size: 1.1rem;
}

.side-menu__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-button {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: 1px solid transparent;
  background-color: var(--color-surface-alt);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-muted);
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

.icon-button:hover,
.icon-button:focus-visible {
  background-color: rgba(13, 110, 253, 0.1);
  color: var(--color-primary);
  border-color: rgba(13, 110, 253, 0.25);
  outline: none;
}

.side-menu__content {
  flex: 1;
  overflow-y: auto;
}

.menu-items {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-items .nav-link {
  display: block;
  padding: 10px 12px;
  border-radius: 10px;
  font-weight: 500;
  color: inherit;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-items .nav-link:hover,
.menu-items .nav-link.router-link-active {
  background-color: rgba(13, 110, 253, 0.12);
  color: var(--color-primary);
}

.main-content {
  padding: 32px 24px 48px;
}

@media (max-width: 768px) {
  .menu-toggle {
    top: 16px;
    left: 16px;
    width: 44px;
    height: 44px;
  }

  .main-content {
    padding: 24px 16px;
  }
}
</style>
