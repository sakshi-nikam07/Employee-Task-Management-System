import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";

const emptyForm = { title: "", description: "", priority: "MEDIUM", dueDate: "", assignedTo: "" };

export default function Dashboard() {
  const [tasks, setTasks] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const role = localStorage.getItem("role");
  const nav = useNavigate();

  const load = async () => {
    try {
      setTasks((await api.get("/tasks")).data);
      if (role === "ADMIN") setEmployees((await api.get("/tasks/employees")).data);
    } catch {
      localStorage.clear();
      nav("/");
    }
  };
  useEffect(() => { load(); }, []);

  const addTask = async (e) => {
    e.preventDefault();
    const { assignedTo, ...task } = form;
    if (!task.dueDate) delete task.dueDate;
    await api.post(`/tasks?assignedTo=${assignedTo}`, task);
    setForm(emptyForm);
    load();
  };

  const changeStatus = async (id, status) => {
    await api.put(`/tasks/${id}/status?status=${status}`);
    load();
  };

  const remove = async (id) => {
    if (!window.confirm("Delete this task?")) return;
    await api.delete(`/tasks/${id}`);
    load();
  };

  const logout = () => { localStorage.clear(); nav("/"); };

  return (
    <div className="container">
      <header>
        <h2>Welcome, {localStorage.getItem("name")} ({role})</h2>
        <button onClick={logout}>Logout</button>
      </header>

      {role === "ADMIN" && (
        <form className="task-form" onSubmit={addTask}>
          <input placeholder="Title" required value={form.title}
            onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <input placeholder="Description" value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <select value={form.priority}
            onChange={(e) => setForm({ ...form, priority: e.target.value })}>
            <option>LOW</option><option>MEDIUM</option><option>HIGH</option>
          </select>
          <input type="date" value={form.dueDate}
            onChange={(e) => setForm({ ...form, dueDate: e.target.value })} />
          <select required value={form.assignedTo}
            onChange={(e) => setForm({ ...form, assignedTo: e.target.value })}>
            <option value="">Assign to...</option>
            {employees.map((u) => <option key={u.id} value={u.id}>{u.name} ({u.role})</option>)}
          </select>
          <button type="submit">Add Task</button>
        </form>
      )}

      {tasks.length === 0 && <p>No tasks yet.</p>}

      <div className="grid">
        {tasks.map((t) => (
          <div key={t.id} className={`card ${t.priority.toLowerCase()}`}>
            <h3>{t.title}</h3>
            <p>{t.description}</p>
            <small>Due: {t.dueDate || "—"} | Assigned: {t.assignedTo?.name}</small>
            <select value={t.status} onChange={(e) => changeStatus(t.id, e.target.value)}>
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="DONE">Done</option>
            </select>
            {role === "ADMIN" && <button className="danger" onClick={() => remove(t.id)}>Delete</button>}
          </div>
        ))}
      </div>
    </div>
  );
}
