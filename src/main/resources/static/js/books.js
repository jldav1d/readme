document.addEventListener('DOMContentLoaded', async () => {
    await loadBooks();
});

async function loadBooks() {
    const booksGrid = document.getElementById('books-grid');
    const loading = document.getElementById('loading');
    const emptyState = document.getElementById('empty-state');

    try {
        const response = await fetch(`${API_BASE_URL}/books`);

        if (!response.ok) {
            throw new Error('Failed to fetch books');
        }

        const books = await response.json();

        loading.style.display = 'none';

        if (books.length === 0) {
            emptyState.style.display = 'block';
            return;
        }

        booksGrid.innerHTML = books.map(book => createBookCard(book)).join('');

    } catch (error) {
        console.error('Error loading books:', error);
        loading.innerHTML = '<p class="text-muted">Error loading books. Please try again later.</p>';
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
            <div class="book-placeholder">
                <span>📖</span>
            </div>
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
                            onclick="addToCart(${book.id})"
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

function addToCart(bookId) {
    // TODO: implement add to cart (will require authentication)
    console.log('Add to cart:', bookId);
    alert('Please login to add items to cart');
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}