import React from 'react';

function Login() {
  return (
    <div>
      <h2>Login</h2>
      <form>
        <label>
          email:
          <input type="text" name="email" />
        </label>
        <label>
          Password:
          <input type="password" name="password" />
        </label>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
}

export default Login;