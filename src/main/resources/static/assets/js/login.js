const username = document.querySelector('.username');
const password = document.querySelector('.password');
const btnLoading = document.querySelector('.btn-loading');
const submitBtn = document.querySelector('.submit-btn');
const loginError = document.querySelector('.login-error');
window.onload = function () {
    if (loginError.innerHTML !== '') {
        loginError.className = 'login-error opacity1';
    }
};

function handleSubmit(e) {
    if (username.value === '') {
        loginError.className = 'login-error opacity1';
        loginError.innerHTML = '請輸入帳號';
        return false;
    }
    if (password.value === '') {
        loginError.className = 'login-error opacity1';
        loginError.innerHTML = '請輸入密碼';
        return false
    }
    btnLoading.style.display = 'block';
    submitBtn.style.backgroundColor = '#b3b3b3';
    submitBtn.style.cursor = 'default';
    return true;
}

function inputFocus() {
    loginError.className = 'login-error opacity0';
}
