import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { API } from "../api";

export default function SignUp() {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    username: "",
    password: ""
  });

  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      // Appel au nouveau endpoint dédié aux candidats
      await API.post("/auth/candidate/register", formData);

      setSuccess(true);
      // Redirection vers le login après 1.5 secondes
      setTimeout(() => {
        navigate("/login");
      }, 1500);

    } catch (err) {
      console.error(err);
      // Gestion propre de l'erreur (texte ou objet)
      const msg = err.response?.data || "Une erreur est survenue lors de l'inscription.";
      setError(typeof msg === 'string' ? msg : "Erreur technique.");
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: 500 }}>
      <div className="card shadow p-4">
        <h3 className="text-center mb-4">Inscription Candidat</h3>

        {error && <div className="alert alert-danger">{error}</div>}

        {success ? (
          <div className="alert alert-success text-center">
            <h5>✅ Compte créé avec succès !</h5>
            <p>Redirection vers la page de connexion...</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Nom complet</label>
              <input
                className="form-control"
                name="fullName"
                placeholder="Ex: Jean Dupont"
                value={formData.fullName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Email</label>
              <input
                type="email"
                className="form-control"
                name="email"
                placeholder="jean@example.com"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Nom d'utilisateur (Login)</label>
              <input
                className="form-control"
                name="username"
                placeholder="Choisissez un identifiant"
                value={formData.username}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Mot de passe</label>
              <input
                type="password"
                className="form-control"
                name="password"
                placeholder="Mot de passe sécurisé"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="d-grid gap-2">
              <button type="submit" className="btn btn-primary">
                S'inscrire
              </button>
            </div>
          </form>
        )}

        <div className="mt-3 text-center">
          <p>Déjà un compte ? <Link to="/login">Se connecter</Link></p>
        </div>
      </div>
    </div>
  );
}