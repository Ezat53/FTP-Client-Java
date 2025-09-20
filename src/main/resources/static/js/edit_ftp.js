document.addEventListener('DOMContentLoaded', function() {
    // Load existing assignments and permissions
    const assignments = /*[[${assignments}]]*/ [];
    console.log("Loading assignments:", assignments);
    assignments.forEach(function(assignment) {
        const userId = assignment.userId;
        console.log("Processing assignment for user:", userId, "with permissions:", assignment);
        const userCheckbox = document.getElementById('user_' + userId);
        if (userCheckbox) {
            userCheckbox.checked = true;
            
            // Enable permission checkboxes
            const permissionCheckboxes = document.querySelectorAll(`input[name^="permissions_${userId}"]`);
            permissionCheckboxes.forEach(function(permissionCheckbox) {
                permissionCheckbox.disabled = false;
            });
            
            // Set permissions
            if (assignment.canRead) {
                document.getElementById('read_' + userId).checked = true;
            }
            if (assignment.canWrite) {
                document.getElementById('write_' + userId).checked = true;
            }
            if (assignment.canDelete) {
                document.getElementById('delete_' + userId).checked = true;
            }
            if (assignment.canUpload) {
                document.getElementById('upload_' + userId).checked = true;
            }
        }
    });
    
    // Handle user checkbox changes
    document.querySelectorAll('.user-checkbox').forEach(function(checkbox) {
        checkbox.addEventListener('change', function() {
            const userId = this.value;
            const permissionCheckboxes = document.querySelectorAll(`input[name^="permissions_${userId}"]`);
            
            permissionCheckboxes.forEach(function(permissionCheckbox) {
                permissionCheckbox.disabled = !checkbox.checked;
                if (!checkbox.checked) {
                    permissionCheckbox.checked = false;
                } else if (permissionCheckbox.value === 'read') {
                    permissionCheckbox.checked = true; // Read permission is always checked when user is selected
                }
            });
        });
    });
    
    // Initialize permission checkboxes state
    document.querySelectorAll('.user-checkbox').forEach(function(checkbox) {
        if (!checkbox.checked) {
            const userId = checkbox.value;
            const permissionCheckboxes = document.querySelectorAll(`input[name^="permissions_${userId}"]`);
            permissionCheckboxes.forEach(function(permissionCheckbox) {
                permissionCheckbox.disabled = true;
            });
        }
    });
});