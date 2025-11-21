import React, { useEffect, useMemo, useState, useCallback } from "react";
import { API } from "../api";
import { useNavigate } from "react-router-dom";

export default function CandidateDashboard() {
  const [offers, setOffers] = useState([]);
  const [candidateId] = useState(localStorage.getItem("userId"));
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  // --- États pour le modal de profil ---
  const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);

  // États pour les champs du formulaire (Titre, Tel, CV)
  const [titre, setTitre] = useState("");
  const [telephone, setTelephone] = useState("");
  const [cv, setCv] = useState(null);

  // États "cachés" (Nom, Email)
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");

  const [profileMessage, setProfileMessage] = useState(null);

  useEffect(() => {
    loadOffers();
  }, []);

  const loadOffers = async () => {
    try {
      const res = await API.get("/offers");
      setOffers(res.data);
    } catch (err) {
      console.error("Erreur chargement offres", err);
    }
  };

  const filtered = useMemo(() => {
    const q = query.toLowerCase();
    return offers.filter(o =>
      o.title?.toLowerCase().includes(q) ||
      o.skills?.toLowerCase().includes(q) ||
      o.description?.toLowerCase().includes(q) ||
      (o.rh?.nomEntreprise?.toLowerCase().includes(q)) // Recherche aussi par entreprise
    );
  }, [offers, query]);


  const handleApply = async (offer) => {
    if (!candidateId) {
      alert("Erreur: ID Candidat non trouvé.");
      return;
    }
    try {
      await API.post(`/candidates/${candidateId}/evaluate`, {
        jobDescription: offer.description,
        offerId: offer.id,
      });
      alert("✅ Candidature envoyée avec succès !");
    } catch (err) {
      console.error(err);
      if (err.response?.data?.includes("CV not found")) {
         alert("❌ Vous devez d'abord ajouter un CV à votre profil pour postuler.");
         handleOpenProfileModal();
      } else {
         alert("❌ Erreur lors de la candidature.");
      }
    }
  };

  // --- Logique du Modal de Profil ---
  const loadProfile = useCallback(async () => {
    if (!candidateId) return;
    try {
      const res = await API.get(`/candidates/${candidateId}`);
      setFullName(res.data.fullName || "");
      setEmail(res.data.email || "");
      setTitre(res.data.titre || "");
      setTelephone(res.data.telephone || "");
    } catch (err) {
      console.error("Erreur chargement profil", err);
    }
  }, [candidateId]);

  const handleOpenProfileModal = async () => {
    await loadProfile();
    setProfileMessage(null);
    setIsProfileModalOpen(true);
  };

  const handleProfileUpdate = async (e) => {
    e.preventDefault();
    setProfileMessage(null);

    try {
      await API.put(`/candidates/${candidateId}`, {
        fullName, email, titre, telephone
      });
      setProfileMessage({ type: "success", text: "Profil mis à jour !" });
    } catch (err) {
      console.error("Erreur MAJ Profil:", err);
      setProfileMessage({ type: "danger", text: "Erreur (infos) !" });
      return;
    }

    if (cv) {
      try {
        const data = new FormData();
        data.append("file", cv);
        await API.post(`/candidates/${candidateId}/cv`, data, {
          headers: { "Content-Type": "multipart/form-data" }
        });
        setProfileMessage({ type: "success", text: "Profil et CV mis à jour !" });
        setCv(null);
      } catch (err) {
        console.error("Erreur Upload CV:", err);
        setProfileMessage({ type: "danger", text: "Erreur (CV) !" });
      }
    }
  };


  return (
    <div className="container my-4">
       <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>🔎 Offres disponibles</h2>
        <div>
          <button className="btn btn-outline-primary me-2" onClick={handleOpenProfileModal}>
            Gérer mon profil
          </button>
          <button className="btn btn-outline-secondary"
                  onClick={() => { localStorage.clear(); window.location.reload(); }}>
            Déconnexion
          </button>
        </div>
      </div>

      <input className="form-control mb-3"
             placeholder="Filtrer par titre / compétence / entreprise"
             value={query}
             onChange={e => setQuery(e.target.value)} />

      <div className="row">
        {filtered.map(o => (
          <div key={o.id} className="col-md-12">
            <div className="card mb-4 shadow-sm">
              <div className="card-body">

                <div className="d-flex justify-content-between">
                    <h5 className="card-title text-primary">{o.title}</h5>
                    <small className="text-muted">Publié le {o.createdAt ? new Date(o.createdAt).toLocaleDateString() : 'Récemment'}</small>
                </div>

                {/* --- NOUVEAU BLOC : INFOS ENTREPRISE (RH) --- */}
                {o.rh && (
                    <div className="alert alert-light border py-2 px-3 small mb-3">
                        <strong className="text-dark">🏢 {o.rh.nomEntreprise || "Entreprise confidentielle"}</strong>
                        <div className="mt-1 text-muted">
                            {o.rh.siteWebEntreprise && (
                                <span className="me-3">
                                    🌐 <a href={o.rh.siteWebEntreprise} target="_blank" rel="noreferrer" className="text-decoration-none">{o.rh.siteWebEntreprise}</a>
                                </span>
                            )}
                            {o.rh.adresseEntreprise && (
                                <span>📍 {o.rh.adresseEntreprise}</span>
                            )}
                        </div>
                    </div>
                )}
                {/* --- FIN DU BLOC --- */}

                <div className="d-flex flex-wrap gap-2 mb-3">
                  {o.skills && o.skills.split(',').filter(s => s).map(skill => (
                    <span key={skill} className="badge bg-secondary fw-normal">{skill}</span>
                  ))}
                </div>

                <div className="row small text-muted border-top border-bottom py-3 mb-3 mx-0 bg-light rounded">
                  <div className="col-sm-6 col-lg-3 mb-2">
                    <strong>Contrat :</strong> {o.typeContrat || 'Non spécifié'}
                  </div>
                  <div className="col-sm-6 col-lg-3 mb-2">
                    <strong>Durée :</strong> {o.duree || 'Non spécifié'}
                  </div>
                  <div className="col-sm-6 col-lg-3 mb-2">
                    <strong>Rémunération :</strong> {o.remuneration || 'Non spécifié'}
                  </div>
                  <div className="col-sm-6 col-lg-3 mb-2">
                    <strong>Expérience :</strong> {o.experience || 'Non spécifié'}
                  </div>
                </div>

                <p
                  className="card-text mb-4"
                  style={{
                    whiteSpace: 'pre-wrap',
                    maxHeight: '250px',
                    overflowY: 'auto',
                    padding: '10px',
                  }}
                >
                  {o.description || 'Aucune description fournie.'}
                </p>

                <button
                  className="btn btn-success"
                  onClick={() => handleApply(o)}
                >
                  Postuler avec mon profil
                </button>
              </div>
            </div>
          </div>
        ))}

        {!filtered.length && (
            <div className="text-center py-5">
                <h5 className="text-muted">Aucune offre ne correspond à votre recherche.</h5>
            </div>
        )}
      </div>

      {/* --- MODAL DE PROFIL --- */}
      {isProfileModalOpen && (
        <>
          <div className="modal-backdrop fade show" style={{ display: 'block' }}></div>
          <div className="modal fade show" style={{ display: 'block' }} tabIndex="-1">
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content">
                <form onSubmit={handleProfileUpdate}>
                  <div className="modal-header">
                    <h5 className="modal-title">Mon Profil (Infos clés)</h5>
                    <button type="button" className="btn-close" onClick={() => setIsProfileModalOpen(false)}></button>
                  </div>
                  <div className="modal-body">
                    <p className="small text-muted">Modifiez vos infos pour vos candidatures.</p>
                    {profileMessage && <div className={`alert alert-${profileMessage.type} small py-2`}>{profileMessage.text}</div>}
                    <div className="mb-3">
                      <label className="form-label">Titre</label>
                      <input className="form-control" value={titre} onChange={(e) => setTitre(e.target.value)} placeholder="Développeur..." />
                    </div>
                    <div className="mb-3">
                      <label className="form-label">Téléphone</label>
                      <input className="form-control" value={telephone} onChange={(e) => setTelephone(e.target.value)} placeholder="06..." />
                    </div>
                    <div className="mb-3">
                      <label className="form-label">CV (PDF)</label>
                      <input type="file" className="form-control" accept="application/pdf" onChange={(e) => setCv(e.target.files[0])} />
                    </div>
                  </div>
                  <div className="modal-footer">
                    <button type="button" className="btn btn-secondary" onClick={() => setIsProfileModalOpen(false)}>Fermer</button>
                    <button type="submit" className="btn btn-primary">Enregistrer</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}