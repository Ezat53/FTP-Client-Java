 // Show error modal if there's an error parameter in URL
 document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    
    if (error) {
        // Decode the error message
        const errorMessage = decodeURIComponent(error);
        
        // Set the error message in the modal
        document.getElementById('errorMessage').innerHTML = '<p class="mb-0">' + errorMessage + '</p>';
        
        // Show the modal
        const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
        errorModal.show();
        
        // Clean up URL by removing error parameter
        const newUrl = window.location.pathname;
        window.history.replaceState({}, document.title, newUrl);
    }
});

function retryConnection() {
    // Close the modal
    const errorModal = bootstrap.Modal.getInstance(document.getElementById('errorModal'));
    errorModal.hide();
    
    // Reload the page to retry
    window.location.reload();
}