<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const isMenuOpen = ref(false);
const isPinned = ref(false);
let menuHoverTimeout = null;

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

const closeMenu = () => {
  if (!isPinned.value) {
    isMenuOpen.value = false;
  }
};

const keepMenuOpen = () => {
  if (menuHoverTimeout) {
    clearTimeout(menuHoverTimeout);
  }
};

const autoCloseMenu = () => {
  if (!isPinned.value && isMenuOpen.value) {
    menuHoverTimeout = setTimeout(() => {
      isMenuOpen.value = false;
    }, 300); // Adjust delay as needed
  }
};

const togglePin = () => {
  isPinned.value = !isPinned.value;
};

// Optional: Close menu on Escape key press
const handleEsc = (event) => {
  if (event.key === 'Escape' && isMenuOpen.value && !isPinned.value) {
    closeMenu();
  }
};

onMounted(() => {
  document.addEventListener('keydown', handleEsc);
});

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleEsc);
  if (menuHoverTimeout) {
    clearTimeout(menuHoverTimeout);
  }
});
</script>

<template>
  <div id="app-container">
    <!-- Golf Menu Button -->
    <button
      class="golf-ball-button"
      @click="toggleMenu"
      aria-label="Toggle navigation"
    >
      <div class="golf-ball-icon">
        <div></div><!-- Middle line of the hamburger icon -->
      </div>
    </button>

    <!-- Slide-out Menu -->
    <div 
      class="side-menu" 
      :class="{ open: isMenuOpen }"
      @mouseenter="keepMenuOpen"
      @mouseleave="autoCloseMenu"
    >
      <div class="side-menu-header">
        <h5>Menu</h5>
        <div class="button-group">
          <button
            class="pin-button"
            @click="togglePin"
            :class="{ pinned: isPinned }"
            aria-label="Pin menu"
          >
            📌
          </button>
          <button
            class="close-button"
            @click="closeMenu"
            aria-label="Close menu"
          >
            ✖
          </button>
        </div>
      </div>
      <div class="side-menu-content">
        <ul class="menu-items">
          <li>
            <router-link to="/" class="nav-link" @click="closeMenu">Home</router-link>
          </li>
          <li>
            <router-link to="/upload" class="nav-link" @click="closeMenu">Upload CSV</router-link>
          </li>
        </ul>
      </div>
    </div>

    <!-- Main Content Area - Router View -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<style>
/* Golf Ball Button */
.golf-ball-button {
  position: fixed;
  top: 20px;
  left: 20px;
  width: 50px;
  height: 50px;
  border-radius: 5px;
  background-color: white;
  border: 1px solid #ccc;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.golf-ball-button:hover {
  background-color: #f8f9fa;
}

/* Replace golf ball icon with hamburger lines */
.golf-ball-icon {
  width: 24px;
  height: 18px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.golf-ball-icon::before,
.golf-ball-icon::after,
.golf-ball-icon div {
  content: '';
  background-color: #333;
  height: 3px;
  width: 100%;
  border-radius: 3px;
}

/* Side Menu */
.side-menu {
  position: fixed;
  top: 0;
  left: -250px; /* Start off-screen */
  width: 250px;
  height: 100vh;
  background-color: white;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
  transition: left 0.3s ease;
  z-index: 1050;
}

.side-menu.open {
  left: 0; /* Slide in */
}

.side-menu-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.side-menu-header h5 {
  margin: 0;
  font-size: 1.25rem;
}

.button-group {
  display: flex;
  gap: 10px;
}

.pin-button, .close-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
}

.pin-button.pinned {
  color: #0d6efd; /* Bootstrap primary color */
}

.side-menu-content {
  padding: 15px;
}

.menu-items {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-items li {
  margin-bottom: 10px;
}

.menu-items a {
  display: block;
  padding: 8px 10px;
  color: #212529;
  text-decoration: none;
  border-radius: 4px;
}

.menu-items a:hover, .menu-items a.active {
  background-color: #f8f9fa;
  color: #0d6efd; /* Bootstrap primary color */
}

/* Add margin for the fixed menu button */
.main-content {
  padding-top: 1rem; 
  min-height: 100vh;
}
</style>
