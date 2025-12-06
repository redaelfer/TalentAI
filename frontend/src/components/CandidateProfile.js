import React, { useState, useEffect, useCallback } from "react"; // 1. Importer useCallback
import { API } from "../api";
import { useNavigate } from "react-router-dom";

export default function CandidateProfile() {
  const [candidateId] = useState(localStorage.getItem("userId"));
  const navigate = useNavigate();

  const [isEditing, setIsEditing] = useState(false);

  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [telephone, setTelephone] = useState("");
  const [titre, setTitre] = useState("");

  const [cv, setCv] = useState(null);
  const [message, setMessage] = useState(null);

  const loadProfile = useCallback(async () => {
    if (!candidateId) {
      setMessage({ type: "danger", text: "Erreur: ID Candidat non trouvé. Veuillez vous reconnecter." });
      return;
    }
    try {
      const res = await API.get(`/candidates/${candidateId}`);
      const { data } = res;

      setFullName(data.fullName || "");
      setEmail(data.email || "");
      setTelephone(data.telephone || "");
      setTitre(data.titre || "");

    } catch (err) {
      console.error("Erreur chargement profil:", err);
      setMessage({ type: "danger", text: "Impossible de charger le profil." });
    }
  }, [candidateId]);
  useEffect(() => {
    loadProfile();
  }, [loadProfile]);


  const handleCancelEdit = () => {
    setIsEditing(false);
    loadProfile();
  };

  const handleProfileUpdate = async (e) => {
    e.preventDefault();
    setMessage(null);

    try {
      await API.put(`/candidates/${candidateId}`, {
        fullName,
        email,
        telephone,
        titre,
      });
      setMessage({ type: "success", text: "✅ Profil mis à jour !" });

    } catch (err) {
      console.error("Erreur MAJ Profil:", err);
      setMessage({ type: "danger", text: "❌ Erreur lors de la mise à jour du profil." });
      return;
    }

    if (cv) {
      try {
        const data = new FormData();
        data.append("file", cv);
        await API.post(`/candidates/${candidateId}/cv`, data, {
          headers: { "Content-Type": "multipart/form-data" }
        });
        setMessage({ type: "success", text: "✅ Profil et CV mis à jour !" });
        setCv(null);

      } catch (err) {
        console.error("Erreur Upload CV:", err);
        setMessage({ type: "danger", text: "❌ Profil mis à jour, mais erreur lors de l'envoi du CV." });
      }
    }

    setIsEditing(false);
  };

  return (
    <div className="container mt-4" style={{ maxWidth: 600 }}>
      <button className="btn btn-link px-0 mb-2" onClick={() => navigate("/")}>
        ⬅ Retour au Dashboard
      </button>

      <div className="d-flex justify-content-between align-items-center">
        <h3>🧍 Profil candidat</h3>
        {!isEditing && (
          <button
            className="btn btn-outline-primary"
            onClick={() => setIsEditing(true)}
            title="Modifier le profil"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-pencil" viewBox="0 0 16 16">
              <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207zm1.586 3L10.5 3.207 4 9.707V12h2.293z"/>
            </svg>
          </button>
        )}
      </div>

      <p className="text-muted">
        Vos informations sont utilisées pour postuler plus rapidement aux offres.
      </p>

      {message && <div className={`alert alert-${message.type} mt-3`}>{message.text}</div>}

      <form onSubmit={handleProfileUpdate}>
        <div className="mb-3">
          <label className="form-label">Nom complet</label>
          <input
            className="form-control"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            disabled={!isEditing}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            type="email"
            className="form-control"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={!isEditing}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Titre (ex: Développeur Fullstack)</label>
          <input
            className="form-control"
            value={titre}
            placeholder={!isEditing ? "Non défini" : "Ajoutez votre titre"}
            onChange={(e) => setTitre(e.target.value)}
            disabled={!isEditing}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Téléphone</label>
          <input
            className="form-control"
            value={telephone}
            placeholder={!isEditing ? "Non défini" : "Ajoutez votre numéro"}
            onChange={(e) => setTelephone(e.target.value)}
            disabled={!isEditing}
          />
        </div>

        {isEditing && (
          <>
            <hr />
            <div className="mb-3">
              <label className="form-label">Changer mon CV (PDF)</label>
              <input
                type="file"
                className="form-control"
                accept="application/pdf"
                onChange={(e) => setCv(e.target.files[0])}
              />
              <div className="form-text">Laissez vide pour conserver votre CV actuel.</div>
            </div>
          </>
        )}

        {isEditing && (
          <div className="d-flex gap-2">
            <button type="submit" className="btn btn-primary">Enregistrer</button>
            <button type="button" className="btn btn-secondary" onClick={handleCancelEdit}>
              Annuler
            </button>
          </div>
        )}
      </form>
    </div>
  );
}