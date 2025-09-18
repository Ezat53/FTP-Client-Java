:root {
  --color-dark-theme: hsl(209, 23%, 22%);
  --color-light-theme: hsl(0, 0%, 98%);
  --color-background: --color-light-theme;
}

@media (prefers-color-scheme: dark) {
  :root {
    --color-background: var(--color-dark-theme);
  }
}

*,
*::before,
*::after {
  box-sizing: border-box;
}

body {
  font-family: "Roboto", sans-serif;
  background-color: var(--color-background);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  transition: background-color 200ms linear;
  position: relative;
}

.suggestion {
  font-size: 2rem;
  top: 10%;
  position: absolute;
  text-align: center;
  transform: translateX(20px);
}

.light-theme {
  --color-background: var(--color-light-theme);
  #sunny {
    animation: 200ms linear forwards rotateToDisappear;
  }
  #moon {
    opacity: 0;
    animation: 200ms linear 200ms forwards appears;
  }

  .suggestion {
    color: var(--color-dark-theme);
  }
}

.dark-theme {
  --color-background: var(--color-dark-theme);
  #moon {
    animation: 200ms linear forwards rotateToDisappear;
  }
  #sunny {
    opacity: 0;
    animation: 200ms linear 200ms forwards appears;
  }

  .suggestion {
    color: var(--color-light-theme);
  }
}

.theme-toggle {
  cursor: pointer;
  background-color: transparent;
  border: none;

  &:focus {
    outline: none;
  }
}

ion-icon {
  position: absolute;
  font-size: 50px;
}

ion-icon[name="sunny"] {
  color: var(--color-light-theme);
}

ion-icon[name="moon"] {
  color: var(--color-dark-theme);
}

@keyframes rotateToDisappear {
  0% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translateY(20px);
  }
}

@keyframes appears {
  0% {
    transform: translateY(-20px);
    opacity: 0;
  }
  100% {
    opacity: 1;
    z-index: 1000;
  }
}


//Based and Inspire in Kevin Powell's "Google Font Light/Dark Mode Toggle" https://codepen.io/kevinpowell/pen/PomqjxO

const themeToggle = document.querySelector(".theme-toggle");

const bodyClassList = document.body.classList;

themeToggle.addEventListener("click", () => {
  bodyClassList.contains("light-theme") ? enableDarkMode() : enableLightMode();
});

function enableDarkMode() {
  bodyClassList.remove("light-theme");
  bodyClassList.add("dark-theme");
}

function enableLightMode() {
  bodyClassList.remove("dark-theme");
  bodyClassList.add("light-theme");
}

const setThemePreference = () => {
  if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
    enableDarkMode();
    return;
  }

  enableLightMode();
};

document.onload = setThemePreference();
