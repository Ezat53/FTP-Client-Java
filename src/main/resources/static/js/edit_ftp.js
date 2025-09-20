document.addEventListener('DOMContentLoaded', function() {
    // Load existing assignments and permissions
    const assignmentsData = window.assignmentsData || {};
    console.log("Loading assignments data:", assignmentsData);
    console.log("Assignments data type:", typeof assignmentsData);
    console.log("Assignments data keys:", Object.keys(assignmentsData));
    
    // Process assignments data
    if (assignmentsData && Object.keys(assignmentsData).length > 0) {
        console.log("Processing assignments...");
        Object.values(assignmentsData).forEach(function(assignment) {
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
                
                // Set permissions (with null checks)
                if (assignment.canRead === true) {
                    const readCheckbox = document.getElementById('read_' + userId);
                    if (readCheckbox) readCheckbox.checked = true;
                }
                if (assignment.canWrite === true) {
                    const writeCheckbox = document.getElementById('write_' + userId);
                    if (writeCheckbox) writeCheckbox.checked = true;
                }
                if (assignment.canDelete === true) {
                    const deleteCheckbox = document.getElementById('delete_' + userId);
                    if (deleteCheckbox) deleteCheckbox.checked = true;
                }
                if (assignment.canUpload === true) {
                    const uploadCheckbox = document.getElementById('upload_' + userId);
                    if (uploadCheckbox) uploadCheckbox.checked = true;
                }
            }
        });
    } else {
        console.log("No assignments found or assignments data is empty");
    }
    
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