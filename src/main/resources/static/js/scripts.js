// ClubHub - Consolidated JavaScript

document.addEventListener("DOMContentLoaded", () => {
  // Initialize all interactive features
  initializeButtons()
  initializeCards()
  initializeForms()
  initializeNavigation()
})

// Button interactions
function initializeButtons() {
  // Join club buttons
  const joinButtons = document.querySelectorAll(".btn-join")
  joinButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.preventDefault()
      const clubName = this.closest(".club-card")?.querySelector("h3")?.textContent || "this club"

      // Simple animation
      this.style.transform = "scale(0.95)"
      setTimeout(() => {
        this.style.transform = "scale(1)"
        this.textContent = "Joined!"
        this.style.background = "#10b981"
        setTimeout(() => {
          this.textContent = "Join Club"
          this.style.background = ""
        }, 2000)
      }, 150)

      // You can add actual join logic here
      console.log(`Joining ${clubName}`)
    })
  })

  // Like buttons
  const likeButtons = document.querySelectorAll(".action-btn")
  likeButtons.forEach((button) => {
    if (button.textContent.includes("👍")) {
      button.addEventListener("click", function (e) {
        e.preventDefault()
        const countSpan = this.querySelector("span:last-child")
        if (countSpan && !isNaN(countSpan.textContent)) {
          const currentCount = Number.parseInt(countSpan.textContent)
          countSpan.textContent = currentCount + 1

          // Visual feedback
          this.style.color = "#2563eb"
          this.style.background = "rgba(37, 99, 235, 0.1)"
        }
      })
    }
  })

  // Floating action button
  const newPostBtn = document.querySelector(".new-post-btn")
  if (newPostBtn) {
    newPostBtn.addEventListener("click", () => {
      // Add your new post logic here
      alert("New post feature coming soon!")
    })
  }
}

// Card hover effects
function initializeCards() {
  const cards = document.querySelectorAll(".club-card, .post-card")

  cards.forEach((card) => {
    card.addEventListener("mouseenter", function () {
      this.style.transform = "translateY(-4px)"
    })

    card.addEventListener("mouseleave", function () {
      this.style.transform = "translateY(0)"
    })
  })
}

// Form enhancements
function initializeForms() {
  // Auto-resize textareas
  const textareas = document.querySelectorAll(".form-textarea")
  textareas.forEach((textarea) => {
    textarea.addEventListener("input", function () {
      this.style.height = "auto"
      this.style.height = this.scrollHeight + "px"
    })
  })

  // Form validation feedback
  const forms = document.querySelectorAll("form")
  forms.forEach((form) => {
    form.addEventListener("submit", (e) => {
      const requiredFields = form.querySelectorAll("[required]")
      let isValid = true

      requiredFields.forEach((field) => {
        if (!field.value.trim()) {
          field.style.borderColor = "#ef4444"
          isValid = false
        } else {
          field.style.borderColor = "#10b981"
        }
      })

      if (!isValid) {
        e.preventDefault()
        alert("Please fill in all required fields.")
      }
    })
  })
}

// Navigation enhancements
function initializeNavigation() {
  // Highlight current page in navigation
  const currentPath = window.location.pathname
  const navLinks = document.querySelectorAll(".nav-link")

  navLinks.forEach((link) => {
    if (link.getAttribute("href") === currentPath) {
      link.style.color = "#2563eb"
      link.style.fontWeight = "600"
    }
  })

  // Mobile menu toggle (if needed)
  const logo = document.querySelector(".logo")
  const navLinks2 = document.querySelector(".nav-links")

  if (window.innerWidth <= 768) {
    logo.addEventListener("click", () => {
      if (navLinks2) {
        navLinks2.style.display = navLinks2.style.display === "flex" ? "none" : "flex"
      }
    })
  }
}

// Utility functions
function showNotification(message, type = "info") {
  const notification = document.createElement("div")
  notification.textContent = message
  notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${type === "success" ? "#10b981" : type === "error" ? "#ef4444" : "#2563eb"};
        color: white;
        border-radius: 8px;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `

  document.body.appendChild(notification)

  setTimeout(() => {
    notification.remove()
  }, 3000)
}

// Add CSS animation for notifications
const style = document.createElement("style")
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
`
document.head.appendChild(style)

// Search functionality (if search input exists)
function initializeSearch() {
  const searchInput = document.querySelector("#search")
  if (searchInput) {
    searchInput.addEventListener("input", function () {
      const searchTerm = this.value.toLowerCase()
      const cards = document.querySelectorAll(".club-card")

      cards.forEach((card) => {
        const clubName = card.querySelector("h3").textContent.toLowerCase()
        const clubDescription = card.querySelector(".club-description").textContent.toLowerCase()

        if (clubName.includes(searchTerm) || clubDescription.includes(searchTerm)) {
          card.style.display = "block"
        } else {
          card.style.display = "none"
        }
      })
    })
  }
}

// Initialize search if needed
initializeSearch()

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
  anchor.addEventListener("click", function (e) {
    e.preventDefault()
    const target = document.querySelector(this.getAttribute("href"))
    if (target) {
      target.scrollIntoView({
        behavior: "smooth",
        block: "start",
      })
    }
  })
})

// Loading states for buttons
function setButtonLoading(button, isLoading) {
  if (isLoading) {
    button.disabled = true
    button.style.opacity = "0.7"
    button.textContent = "Loading..."
  } else {
    button.disabled = false
    button.style.opacity = "1"
  }
}

// Export functions for use in other scripts if needed
window.ClubHub = {
  showNotification,
  setButtonLoading,
  initializeSearch,
}
