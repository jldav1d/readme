let allBooks = [];
let filteredBooks = [];
let currentSearchQuery = '';
let currentCategory = 'all';

const PAGE_SIZE = 10;


// page number counting start with 0
let currentPage = 0;

const paginatePrevBtn = document.querySelector("#prev-btn");
const paginateNextBtn = document.querySelector("#next-btn");

document.addEventListener('DOMContentLoaded', async () => {
    await loadBooks();
    setupEventListeners();
});


function setupEventListeners() {
    // search input with debounce
    const searchInput = document.getElementById('search-input');
    let searchTimeout;

    searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            currentSearchQuery = e.target.value.toLowerCase().trim();
            applyFilters();

            // wait 300ms after user stops typing
        }, 300);
    });

    paginatePrevBtn.addEventListener('click', async (e) => {
        e.preventDefault();

        await loadBooks(currentPage - 1, PAGE_SIZE);
    })
    paginateNextBtn.addEventListener('click', async (e) => {
        e.preventDefault();

        await loadBooks(currentPage + 1, PAGE_SIZE);
    })
}

async function loadBooks(page = 0, pageSize = 10) {
    const booksGrid = document.getElementById('books-grid');
    const loading = document.getElementById('loading');
    const emptyState = document.getElementById('empty-state');
    const totalPagesText = document.querySelector('#total-pages');
    const currentPageText = document.querySelector('#current-page');



    try {
        const response = await fetch(`${API_BASE_URL}/books?page=${page}&pageSize=${pageSize}`);

        if (!response.ok) {
            throw new Error('Failed to fetch books');
        }

        paginatedResponse = await response.json();

        hasNext = paginatedResponse.hasNext;
        hasPrev = paginatedResponse.hasPrev;

        paginatePrevBtn.classList.remove('disabled');
        paginateNextBtn.classList.remove('disabled');


        // note: don't change the values of the api responses
        // for easier development

        totalPages = paginatedResponse.totalPages;
        currentPage = paginatedResponse.currentPage;

        // adjust for zero based counting of page number
        totalPagesText.innerText = totalPages;
        currentPageText.innerText = currentPage + 1;

        if(!hasPrev && currentPage < 1) {
            paginatePrevBtn.classList.add('disabled');
        }

        if (!hasNext && currentPage >= (totalPages - 1)) {
            paginateNextBtn.classList.add('disabled');
        }

        allBooks = paginatedResponse.content;

        filteredBooks = [...allBooks];

        loading.style.display = 'none';

        if (allBooks.length === 0) {
            emptyState.style.display = 'block';
            updateResultsCount(0);
            return;
        }

        // display categories
        loadCategories();

        // display all books initially
        displayBooks(filteredBooks);
        updateResultsCount(filteredBooks.length);

    } catch (error) {
        console.error('Error loading books:', error);
        loading.innerHTML = '<p class="text-muted">Error loading books. Please try again later.</p>';
    }
}

function loadCategories() {
    const categoryFiltersContainer = document.getElementById('category-filters');

    // reset for pagination actions
    categoryFiltersContainer.innerHTML = "";

    // extract unique categories from all books
    const categoriesSet = new Set();
    allBooks.forEach(book => {
        if (book.categories && book.categories.length > 0) {
            book.categories.forEach(category => categoriesSet.add(category));
        }
    });

    const categories = Array.from(categoriesSet).sort();

    // retain the "All Books" button and add category buttons
    const categoryButtons = categories.map(category =>
        `<button class="filter-btn" data-category="${escapeHtml(category)}">
            ${escapeHtml(category)}
        </button>`
    ).join('');

    categoryFiltersContainer.innerHTML += categoryButtons;

    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {

            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));

            e.target.classList.add('active');

            // update current category and apply filters
            currentCategory = e.target.dataset.category;
            applyFilters();
        });
    });
}

function applyFilters() {
    filteredBooks = [...allBooks];

    // apply category filter
    if (currentCategory !== 'all') {
        filteredBooks = filteredBooks.filter(book =>
            book.categories && book.categories.includes(currentCategory)
        );
    }

    // apply search filter
    if (currentSearchQuery) {
        filteredBooks = filteredBooks.filter(book =>
            book.title.toLowerCase().includes(currentSearchQuery)
        );
    }

    // Display filtered results
    displayBooks(filteredBooks);
    updateResultsCount(filteredBooks.length);
}

function displayBooks(books) {
    const booksGrid = document.getElementById('books-grid');
    const emptyState = document.getElementById('empty-state');

    if (books.length === 0) {
        booksGrid.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }

    emptyState.style.display = 'none';
    booksGrid.innerHTML = books.map(book => createBookCard(book)).join('');
}

function updateResultsCount(count) {
    const resultsCountElement = document.getElementById('results-count');

    if (count === 0) {
        resultsCountElement.textContent = 'No books found';
    } else if (count === allBooks.length) {
        resultsCountElement.textContent = `Showing all ${count} book${count !== 1 ? 's' : ''}`;
    } else {
        resultsCountElement.textContent = `Showing ${count} of ${allBooks.length} book${allBooks.length !== 1 ? 's' : ''}`;
    }
}


function createBookCard(book) {
    const stockClass = book.stock > 10 ? 'in-stock' : book.stock > 0 ? 'low-stock' : 'out-of-stock';
    const stockText = book.stock > 0 ? `${book.stock} in stock` : 'Out of stock';

    const categories = book.categories && book.categories.length > 0
        ? `<div class="book-categories">
            ${book.categories.map(cat => `<span class="category-badge">${cat}</span>`).join('')}
           </div>`
        : '';

    return `
        <div class="book-card">
<!--        TODO: add book image -->
<!--            <div class="book-placeholder">-->
<!--                <span>📖</span>-->
<!--            </div>-->
            <div class="book-content">
                ${categories}
                <h3 class="book-title">${escapeHtml(book.title)}</h3>
                <p class="book-author">by ${escapeHtml(book.author)}</p>
                <p class="book-description">${escapeHtml(book.description)}</p>
                <div class="book-footer">
                    <span class="book-price">$${book.price.toFixed(2)}</span>
                    <span class="book-stock ${stockClass}">${stockText}</span>
                </div>
                <div class="book-actions">
                    <button class="btn btn-secondary" onclick="viewBookDetails(${book.id})">
                        Details
                    </button>
                    <button class="btn btn-primary" 
                            onclick="addToCart(event, ${book.id})"
                            ${book.stock === 0 ? 'disabled' : ''}>
                        Add to Cart
                    </button>
                </div>
            </div>
        </div>
    `;
}

function viewBookDetails(bookId) {
    // TODO: implement book details view
    console.log('View details for book:', bookId);
    alert('Book details feature coming soon!');
}

async function addToCart(event, bookId) {
    const authenticated = await isAuthenticated();

    if (!authenticated) {
        if (confirm('Please login to add items to your cart. Redirect to login page?')) {
            redirectToLogin();
        }
        return;
    }

    const button = event.target;
    const originalText = button.textContent;

    button.disabled = true;
    button.textContent = 'Adding...';

    try {
        const cart = await addItemToCart(bookId, 1);

        button.textContent = '✓ Added';
        button.style.backgroundColor = '#10b981'; // Green color

        showNotification('Book added to cart successfully!', 'success');

        setTimeout(() => {
            button.textContent = originalText;
            button.disabled = false;
            button.style.backgroundColor = '';
        }, 2000);

    } catch (error) {
        console.error('Error adding to cart:', error);

        showNotification(error.message || 'Failed to add to cart', 'error');

        button.textContent = originalText;
        button.disabled = false;
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}