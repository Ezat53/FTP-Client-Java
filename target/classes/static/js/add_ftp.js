document.addEventListener('DOMContentLoaded', function() {
    // Protocol to port mapping
    const protocolPorts = {
        'ftp': 21,
        'sftp': 22,
        'scp': 22
    };
    
    // Handle protocol changes
    const protocolSelect = document.getElementById('protocol');
    const portInput = document.getElementById('port');
    
    protocolSelect.addEventListener('change', function() {
        const selectedProtocol = this.value;
        if (selectedProtocol && protocolPorts[selectedProtocol]) {
            portInput.value = protocolPorts[selectedProtocol];
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