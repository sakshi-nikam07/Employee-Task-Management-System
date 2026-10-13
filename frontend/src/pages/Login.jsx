import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const nav = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    try {
      const { data } = await api.post("/auth/login", form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("name", data.name);
      nav("/dashboard");
    } catch {
      setError("Invalid email or password");
    }
  };

  return (
    <div className="login-box">
      <h2>Task Manager Login</h2>
      <form onSubmit={submit}>
        <input placeholder="Email" value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="password" placeholder="Password" value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <button type="submit">Login</button>
        {error && <p className="error">{error}</p>}
      </form>
      <p className="hint">Demo: admin@test.com / admin123<br />john@test.com / john123</p>
    </div>
  );
}
