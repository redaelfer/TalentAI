import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { API } from "../api";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isRh, setIsRh] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const url = isRh ? "/auth/rh/login" : "/auth/candidate/login";

    try {
      const res = await API.post(url, { username, password });

      localStorage.setItem("userId", res.data.id);
      localStorage.setItem("username", res.data.username);
      localStorage.setItem("role", res.data.role);

      if (res.data.role === "ROLE_RH") {
        navigate("/rh");
      } else {
        navigate("/");
      }

    } catch (err) {
      console.error(err);
      let errorMsg = "Erreur de connexion";
      if (err.response && err.response.data) {
        errorMsg = typeof err.response.data === 'string'
                   ? err.response.data
                   : (err.response.data.error || JSON.stringify(err.response.data));
      }
      setError(errorMsg === "Invalid credentials" ? "Identifiants incorrects" : errorMsg);
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: 400 }}>
      <div className="card shadow p-4">
        <h3 className="text-center mb-4">Connexion</h3>

        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-check form-switch mb-3">
            <input
              className="form-check-input"
              type="checkbox"
              id="rhSwitch"
              checked={isRh}
              onChange={(e) => setIsRh(e.target.checked)}
            />
            <label className="form-check-label" htmlFor="rhSwitch">
              Je suis un Recruteur (RH)
            </label>
          </div>

          <div className="mb-3">
            <label>Nom d'utilisateur</label>
            <input
              className="form-control"
              placeholder="Entrez votre identifiant"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>

          <div className="mb-3">
            <label>Mot de passe</label>
            <input
              type="password"
              className="form-control"
              placeholder="Entrez votre mot de passe"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <div className="d-grid">
            <button type="submit" className="btn btn-primary">
              Se connecter
            </button>
          </div>
        </form>

        {!isRh && (
          <p className="text-center mt-3">
            Pas de compte ? <Link to="/sign-up">S'inscrire</Link>
          </p>
        )}
      </div>
    </div>
  );
}