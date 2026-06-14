document.addEventListener('DOMContentLoaded', () => {
    initFilters();
    initVoting();
    initReviewsModal();
});

// Toast helper
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.style.cssText = `
        background: #201a16;
        color: #fff;
        padding: 0.8rem 1.2rem;
        border-radius: 8px;
        font-family: "Trebuchet MS", sans-serif;
        font-size: 0.92rem;
        box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        border-left: 4px solid ${type === 'success' ? 'var(--mint, #1f8a70)' : 'var(--tomato, #d94125)'};
        animation: slideIn 200ms ease;
    `;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.transition = 'opacity 300ms ease, transform 300ms ease';
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// Search and Filter Functionality
let activeCategory = 'all';
let searchQuery = '';

function initFilters() {
    const searchInput = document.getElementById('searchInput');
    const categoryFiltersContainer = document.getElementById('categoryFilters');
    const cards = document.querySelectorAll('.food-card');

    if (!categoryFiltersContainer) return;

    // Dynamically collect unique categories from the cards in the DOM
    const categories = new Set();
    cards.forEach(card => {
        const catSpan = card.querySelector('.category');
        if (catSpan && catSpan.textContent.trim()) {
            categories.add(catSpan.textContent.trim());
        }
    });

    // Build filter chips
    let filterHtml = `<button class="btn btn-secondary filter-chip active" data-category="all" style="min-height: auto; padding: 0.4rem 0.95rem; font-size: 0.86rem; border-radius: 999px;">All</button>`;
    categories.forEach(cat => {
        filterHtml += `<button class="btn btn-secondary filter-chip" data-category="${cat}" style="min-height: auto; padding: 0.4rem 0.95rem; font-size: 0.86rem; border-radius: 999px;">${cat}</button>`;
    });
    categoryFiltersContainer.innerHTML = filterHtml;

    // Add click event listeners to filter chips
    const chips = categoryFiltersContainer.querySelectorAll('.filter-chip');
    chips.forEach(chip => {
        chip.addEventListener('click', () => {
            chips.forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            activeCategory = chip.getAttribute('data-category');
            filterCards();
        });
    });

    // Add input event listener to search input
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            searchQuery = e.target.value.toLowerCase().trim();
            filterCards();
        });
    }
}

function filterCards() {
    const cards = document.querySelectorAll('.food-card');
    cards.forEach(card => {
        const name = card.querySelector('h2').textContent.toLowerCase();
        const categorySpan = card.querySelector('.category');
        const category = categorySpan ? categorySpan.textContent.trim() : '';

        const matchesSearch = name.includes(searchQuery);
        const matchesCategory = activeCategory === 'all' || category === activeCategory;

        if (matchesSearch && matchesCategory) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
}

// Helper to color star elements
function highlightStars(stars, count) {
    stars.forEach(star => {
        const val = parseInt(star.getAttribute('data-value'));
        if (val <= count) {
            star.style.color = '#fbbf24'; // Warm amber gold
        } else {
            star.style.color = 'rgba(255,255,255,0.15)';
        }
    });
}

// AJAX Voting
function initVoting() {
    const forms = document.querySelectorAll('.vote-form');
    forms.forEach(form => {
        const starRatingContainer = form.querySelector('.star-rating');
        const ratingInput = form.querySelector('input[name="rating"]');
        let stars = [];

        if (starRatingContainer && ratingInput) {
            stars = starRatingContainer.querySelectorAll('.star');
            stars.forEach(star => {
                const val = parseInt(star.getAttribute('data-value'));

                star.addEventListener('mouseenter', () => {
                    highlightStars(stars, val);
                });

                star.addEventListener('click', () => {
                    ratingInput.value = val;
                    highlightStars(stars, val);
                });
            });

            starRatingContainer.addEventListener('mouseleave', () => {
                const currentVal = parseInt(ratingInput.value) || 0;
                highlightStars(stars, currentVal);
            });
        }

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const foodItemId = parseInt(form.querySelector('input[name="foodItemId"]').value);
            const voterName = form.querySelector('input[name="voterName"]').value.trim();
            const rating = parseInt(ratingInput.value);
            const comment = form.querySelector('textarea[name="comment"]').value.trim();

            if (!voterName) {
                showToast("Voter name is required", "error");
                return;
            }

            if (!rating || rating < 1 || rating > 5) {
                showToast("Please select a rating", "error");
                return;
            }

            const payload = {
                foodItemId,
                voterName,
                rating,
                comment: comment || null
            };

            try {
                const response = await fetch('/api/votes', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    showToast("Vote submitted successfully!", "success");
                    form.reset();
                    if (ratingInput) ratingInput.value = '';
                    if (stars.length > 0) highlightStars(stars, 0);
                } else {
                    const errData = await response.json();
                    const errMsg = errData.detail || "Error submitting vote";
                    showToast(errMsg, "error");
                }
            } catch (err) {
                console.error(err);
                showToast("Server connection error. Please try again.", "error");
            }
        });
    });
}

// Reviews Modal
function initReviewsModal() {
    const modal = document.getElementById('reviewsModal');
    const modalFoodName = document.getElementById('modalFoodName');
    const modalVotesList = document.getElementById('modalVotesList');
    const closeModalBtn = document.getElementById('closeModalBtn');

    if (!modal) return;

    // Use event delegation for View Reviews buttons
    document.body.addEventListener('click', async (e) => {
        if (e.target.classList.contains('view-reviews-btn')) {
            const foodId = e.target.getAttribute('data-food-id');
            const foodName = e.target.getAttribute('data-food-name');

            modalFoodName.textContent = foodName;
            modalVotesList.innerHTML = '<div style="text-align: center; color: var(--muted); padding: 2rem;">Loading reviews...</div>';
            modal.style.display = 'flex';

            try {
                const response = await fetch(`/api/votes/food-item/${foodId}`);
                if (response.ok) {
                    const votes = await response.json();
                    renderModalVotes(votes);
                } else {
                    modalVotesList.innerHTML = '<div style="color: var(--tomato); text-align: center;">Failed to load reviews.</div>';
                }
            } catch (err) {
                console.error(err);
                modalVotesList.innerHTML = '<div style="color: var(--tomato); text-align: center;">Error connecting to server.</div>';
            }
        }
    });

    // Close modal handlers
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });
}

function renderModalVotes(votes) {
    const container = document.getElementById('modalVotesList');
    if (votes.length === 0) {
        container.innerHTML = '<div style="text-align: center; color: var(--muted); padding: 2rem;">No reviews or comments yet. Be the first to vote!</div>';
        return;
    }

    let html = '';
    votes.forEach(vote => {
        const dateStr = new Date(vote.createdAt).toLocaleString(undefined, {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        // Generate stars
        let stars = '';
        for (let i = 1; i <= 5; i++) {
            stars += i <= vote.rating 
                ? '<span style="color: var(--tomato, #d94125); margin-right: 0.1rem;">★</span>' 
                : '<span style="color: #eaded0; margin-right: 0.1rem;">★</span>';
        }

        html += `
            <div style="background: #fffdf9; border: 1px solid var(--line); border-radius: 8px; padding: 1rem; display: flex; flex-direction: column; gap: 0.4rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
                    <span style="font-weight: 800; font-family: 'Trebuchet MS', sans-serif; color: var(--ink);">${escapeHtml(vote.voterName)}</span>
                    <span style="font-size: 0.8rem; color: var(--muted);">${dateStr}</span>
                </div>
                <div style="font-size: 1.1rem; line-height: 1;">
                    ${stars}
                </div>
                <p style="color: var(--muted); font-size: 0.95rem; font-style: italic; margin-top: 0.25rem;">
                    ${vote.comment ? escapeHtml(vote.comment) : '<span style="color: #c4b5a6;">(No comment)</span>'}
                </p>
            </div>
        `;
    });
    container.innerHTML = html;
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}
