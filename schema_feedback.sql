-- =============================================================================
-- Coachly Feedback Service - Schema completo
-- Database: coachly_feedback
-- =============================================================================
-- Esegui questo script come superuser o come owner del database.
-- Crea il database manualmente prima se non esiste:
--   CREATE DATABASE coachly_feedback OWNER coachly;
-- Poi connettiti al database e lancia questo script:
--   \c coachly_feedback
-- =============================================================================

-- Estensione per gen_random_uuid() (richiesta da PostgreSQL < 13)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =============================================================================
-- SCHEMA
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS feedback;

COMMENT ON SCHEMA feedback IS 'Schema del microservizio Coachly Feedback: feedback utente, feature request, commenti, poll e moderazione.';

SET search_path = feedback;

-- =============================================================================
-- TABELLA: feedback_entries
-- Raccoglie i feedback inviati dagli utenti su diversi target (feature, scherm
-- ate, moduli, workflow, ecc.).
-- =============================================================================

CREATE TABLE feedback_entries (
    id               UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id   UUID        NOT NULL,
    type             VARCHAR(30) NOT NULL
        CONSTRAINT chk_feedback_type
            CHECK (type IN ('REVIEW', 'GENERAL', 'BUG', 'CONTEXTUAL')),
    status           VARCHAR(30) NOT NULL
        CONSTRAINT chk_feedback_status
            CHECK (status IN ('ACTIVE', 'HIDDEN', 'ARCHIVED', 'DELETED')),
    visibility       VARCHAR(30) NOT NULL
        CONSTRAINT chk_feedback_visibility
            CHECK (visibility IN ('PUBLIC', 'HIDDEN', 'INTERNAL')),
    title            VARCHAR(200)  NOT NULL,
    body             VARCHAR(4000) NOT NULL,
    rating_value     INTEGER,
    category         VARCHAR(100),
    target_type      VARCHAR(30) NOT NULL
        CONSTRAINT chk_feedback_target_type
            CHECK (target_type IN ('FEATURE', 'SCREEN', 'MODULE', 'WORKFLOW', 'GENERIC',
                                   'FEATURE_REQUEST', 'FEEDBACK_ENTRY', 'POLL')),
    target_id        UUID        NOT NULL,
    feature_key      VARCHAR(100),
    screen_key       VARCHAR(100),
    flow_key         VARCHAR(100),
    platform         VARCHAR(30),
    app_version      VARCHAR(30),
    severity         VARCHAR(20)
        CONSTRAINT chk_feedback_severity
            CHECK (severity IS NULL OR severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    reproducible     BOOLEAN,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL,
    deleted_at       TIMESTAMPTZ
);

COMMENT ON TABLE  feedback_entries                  IS 'Feedback inviati dagli utenti su feature, schermate, moduli, workflow o entità generiche.';
COMMENT ON COLUMN feedback_entries.id               IS 'Identificativo univoco del feedback (UUID generato automaticamente).';
COMMENT ON COLUMN feedback_entries.author_user_id   IS 'UUID dell''utente che ha inviato il feedback.';
COMMENT ON COLUMN feedback_entries.type             IS 'Tipo di feedback: REVIEW, GENERAL, BUG, CONTEXTUAL.';
COMMENT ON COLUMN feedback_entries.status           IS 'Stato corrente: ACTIVE, HIDDEN, ARCHIVED, DELETED.';
COMMENT ON COLUMN feedback_entries.visibility       IS 'Visibilità: PUBLIC, HIDDEN, INTERNAL.';
COMMENT ON COLUMN feedback_entries.title            IS 'Titolo del feedback (max 200 caratteri).';
COMMENT ON COLUMN feedback_entries.body             IS 'Corpo del feedback (max 4000 caratteri).';
COMMENT ON COLUMN feedback_entries.rating_value     IS 'Voto numerico opzionale associato al feedback.';
COMMENT ON COLUMN feedback_entries.category         IS 'Categoria libera (max 100 caratteri).';
COMMENT ON COLUMN feedback_entries.target_type      IS 'Tipo di entità target: FEATURE, SCREEN, MODULE, WORKFLOW, GENERIC, FEATURE_REQUEST, FEEDBACK_ENTRY, POLL.';
COMMENT ON COLUMN feedback_entries.target_id        IS 'UUID dell''entità target.';
COMMENT ON COLUMN feedback_entries.feature_key      IS 'Chiave identificativa della feature (contesto contestuale).';
COMMENT ON COLUMN feedback_entries.screen_key       IS 'Chiave identificativa della schermata.';
COMMENT ON COLUMN feedback_entries.flow_key         IS 'Chiave identificativa del flusso utente.';
COMMENT ON COLUMN feedback_entries.platform         IS 'Piattaforma di invio (es. ios, android, web).';
COMMENT ON COLUMN feedback_entries.app_version      IS 'Versione dell''app al momento dell''invio.';
COMMENT ON COLUMN feedback_entries.severity         IS 'Severità del bug (solo per type=BUG): LOW, MEDIUM, HIGH, CRITICAL.';
COMMENT ON COLUMN feedback_entries.reproducible     IS 'Flag che indica se il bug è riproducibile.';
COMMENT ON COLUMN feedback_entries.created_at       IS 'Timestamp di creazione (timezone-aware).';
COMMENT ON COLUMN feedback_entries.updated_at       IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN feedback_entries.deleted_at       IS 'Timestamp di cancellazione logica (soft delete).';

-- =============================================================================
-- TABELLA: feature_requests
-- Feature request inviate dagli utenti con supporto a voti, stati e duplicati.
-- =============================================================================

CREATE TABLE feature_requests (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    author_user_id    UUID        NOT NULL,
    title             VARCHAR(200)  NOT NULL,
    description       VARCHAR(4000) NOT NULL,
    category          VARCHAR(100),
    status            VARCHAR(30) NOT NULL
        CONSTRAINT chk_feature_request_status
            CHECK (status IN ('NEW', 'UNDER_REVIEW', 'PLANNED', 'IN_PROGRESS',
                              'RELEASED', 'REJECTED', 'DUPLICATE', 'ARCHIVED')),
    duplicate_of_id   UUID,
    platform_target   VARCHAR(30),
    module_key        VARCHAR(100),
    upvotes_count     INTEGER     NOT NULL DEFAULT 0
        CONSTRAINT chk_fr_upvotes_non_negative CHECK (upvotes_count >= 0),
    comments_count    INTEGER     NOT NULL DEFAULT 0
        CONSTRAINT chk_fr_comments_non_negative CHECK (comments_count >= 0),
    created_at        TIMESTAMPTZ NOT NULL,
    updated_at        TIMESTAMPTZ NOT NULL,
    deleted_at        TIMESTAMPTZ
);

COMMENT ON TABLE  feature_requests                   IS 'Feature request inviate dagli utenti, con stato del ciclo di vita e contatori aggregati.';
COMMENT ON COLUMN feature_requests.id                IS 'Identificativo univoco della feature request.';
COMMENT ON COLUMN feature_requests.author_user_id    IS 'UUID dell''utente che ha aperto la feature request.';
COMMENT ON COLUMN feature_requests.title             IS 'Titolo della feature request (max 200 caratteri).';
COMMENT ON COLUMN feature_requests.description       IS 'Descrizione dettagliata (max 4000 caratteri).';
COMMENT ON COLUMN feature_requests.category          IS 'Categoria libera (max 100 caratteri).';
COMMENT ON COLUMN feature_requests.status            IS 'Stato: NEW, UNDER_REVIEW, PLANNED, IN_PROGRESS, RELEASED, REJECTED, DUPLICATE, ARCHIVED.';
COMMENT ON COLUMN feature_requests.duplicate_of_id   IS 'UUID della feature request originale (per status=DUPLICATE).';
COMMENT ON COLUMN feature_requests.platform_target   IS 'Piattaforma target della feature (es. ios, android, web).';
COMMENT ON COLUMN feature_requests.module_key        IS 'Chiave del modulo applicativo di riferimento.';
COMMENT ON COLUMN feature_requests.upvotes_count     IS 'Contatore denormalizzato degli upvote (>= 0).';
COMMENT ON COLUMN feature_requests.comments_count    IS 'Contatore denormalizzato dei commenti (>= 0).';
COMMENT ON COLUMN feature_requests.created_at        IS 'Timestamp di creazione.';
COMMENT ON COLUMN feature_requests.updated_at        IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN feature_requests.deleted_at        IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: feature_votes
-- Voti degli utenti sulle feature request.
-- Un utente può votare una sola volta per feature request.
-- =============================================================================

CREATE TABLE feature_votes (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_request_id  UUID        NOT NULL
        REFERENCES feature_requests(id),
    user_id             UUID        NOT NULL,
    vote_type           VARCHAR(10) NOT NULL
        CONSTRAINT chk_feature_vote_type
            CHECK (vote_type IN ('UP', 'DOWN', 'LIKE')),
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    deleted_at          TIMESTAMPTZ,
    CONSTRAINT uk_feature_vote_feature_user UNIQUE (feature_request_id, user_id)
);

COMMENT ON TABLE  feature_votes                      IS 'Voti degli utenti sulle feature request. Vincolo: un voto per utente per feature request.';
COMMENT ON COLUMN feature_votes.id                   IS 'Identificativo univoco del voto.';
COMMENT ON COLUMN feature_votes.feature_request_id   IS 'FK verso feature_requests.id.';
COMMENT ON COLUMN feature_votes.user_id              IS 'UUID dell''utente che ha votato.';
COMMENT ON COLUMN feature_votes.vote_type            IS 'Tipo di voto: UP, DOWN, LIKE.';
COMMENT ON COLUMN feature_votes.created_at           IS 'Timestamp di creazione.';
COMMENT ON COLUMN feature_votes.updated_at           IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN feature_votes.deleted_at           IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: comments
-- Commenti con threading annidato (max profondità 4).
-- I campi parent_comment_id e root_comment_id supportano query gerarchiche.
-- =============================================================================

CREATE TABLE comments (
    id                UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type       VARCHAR(30) NOT NULL
        CONSTRAINT chk_comment_target_type
            CHECK (target_type IN ('FEATURE', 'SCREEN', 'MODULE', 'WORKFLOW', 'GENERIC',
                                   'FEATURE_REQUEST', 'FEEDBACK_ENTRY', 'POLL')),
    target_id         UUID        NOT NULL,
    author_user_id    UUID        NOT NULL,
    parent_comment_id UUID,
    root_comment_id   UUID,
    depth             INTEGER     NOT NULL
        CONSTRAINT chk_comment_depth CHECK (depth >= 0 AND depth <= 4),
    body              VARCHAR(3000) NOT NULL,
    status            VARCHAR(30) NOT NULL
        CONSTRAINT chk_comment_status
            CHECK (status IN ('ACTIVE', 'HIDDEN', 'DELETED')),
    score             INTEGER     NOT NULL DEFAULT 0,
    replies_count     INTEGER     NOT NULL DEFAULT 0
        CONSTRAINT chk_comment_replies_non_negative CHECK (replies_count >= 0),
    created_at        TIMESTAMPTZ NOT NULL,
    updated_at        TIMESTAMPTZ NOT NULL,
    deleted_at        TIMESTAMPTZ
);

COMMENT ON TABLE  comments                    IS 'Commenti con threading annidato su qualunque entità target. Profondità massima 4 livelli.';
COMMENT ON COLUMN comments.id                IS 'Identificativo univoco del commento.';
COMMENT ON COLUMN comments.target_type       IS 'Tipo di entità commentata.';
COMMENT ON COLUMN comments.target_id         IS 'UUID dell''entità commentata.';
COMMENT ON COLUMN comments.author_user_id    IS 'UUID dell''autore del commento.';
COMMENT ON COLUMN comments.parent_comment_id IS 'UUID del commento genitore diretto (NULL per commenti radice).';
COMMENT ON COLUMN comments.root_comment_id   IS 'UUID del commento radice del thread (NULL per commenti radice). Usato per query gerarchiche rapide.';
COMMENT ON COLUMN comments.depth             IS 'Livello di annidamento (0 = radice, max 4).';
COMMENT ON COLUMN comments.body              IS 'Testo del commento (max 3000 caratteri).';
COMMENT ON COLUMN comments.status            IS 'Stato: ACTIVE, HIDDEN, DELETED.';
COMMENT ON COLUMN comments.score             IS 'Punteggio aggregato dei voti (può essere negativo).';
COMMENT ON COLUMN comments.replies_count     IS 'Contatore denormalizzato delle risposte dirette (>= 0).';
COMMENT ON COLUMN comments.created_at        IS 'Timestamp di creazione.';
COMMENT ON COLUMN comments.updated_at        IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN comments.deleted_at        IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: comment_votes
-- Voti degli utenti sui commenti.
-- Un utente può votare un commento una sola volta.
-- =============================================================================

CREATE TABLE comment_votes (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id  UUID        NOT NULL
        REFERENCES comments(id),
    user_id     UUID        NOT NULL,
    vote_type   VARCHAR(10) NOT NULL
        CONSTRAINT chk_comment_vote_type
            CHECK (vote_type IN ('UP', 'DOWN', 'LIKE')),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    deleted_at  TIMESTAMPTZ,
    CONSTRAINT uk_comment_vote_comment_user UNIQUE (comment_id, user_id)
);

COMMENT ON TABLE  comment_votes            IS 'Voti degli utenti sui commenti. Vincolo: un voto per utente per commento.';
COMMENT ON COLUMN comment_votes.id         IS 'Identificativo univoco del voto.';
COMMENT ON COLUMN comment_votes.comment_id IS 'FK verso comments.id.';
COMMENT ON COLUMN comment_votes.user_id    IS 'UUID dell''utente che ha votato.';
COMMENT ON COLUMN comment_votes.vote_type  IS 'Tipo di voto: UP, DOWN, LIKE.';
COMMENT ON COLUMN comment_votes.created_at IS 'Timestamp di creazione.';
COMMENT ON COLUMN comment_votes.updated_at IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN comment_votes.deleted_at IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: polls
-- Sondaggi con finestra temporale (opens_at / closes_at).
-- =============================================================================

CREATE TABLE polls (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    title               VARCHAR(200)  NOT NULL,
    description         VARCHAR(4000) NOT NULL,
    type                VARCHAR(30) NOT NULL
        CONSTRAINT chk_poll_type
            CHECK (type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE')),
    status              VARCHAR(20) NOT NULL
        CONSTRAINT chk_poll_status
            CHECK (status IN ('DRAFT', 'PUBLISHED', 'CLOSED', 'ARCHIVED')),
    multiple_choice     BOOLEAN     NOT NULL,
    anonymous_results   BOOLEAN     NOT NULL,
    opens_at            TIMESTAMPTZ NOT NULL,
    closes_at           TIMESTAMPTZ NOT NULL,
    created_by_user_id  UUID        NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    deleted_at          TIMESTAMPTZ,
    CONSTRAINT chk_poll_window CHECK (closes_at > opens_at)
);

COMMENT ON TABLE  polls                     IS 'Sondaggi con finestra di apertura/chiusura e opzioni di configurazione.';
COMMENT ON COLUMN polls.id                  IS 'Identificativo univoco del sondaggio.';
COMMENT ON COLUMN polls.title               IS 'Titolo del sondaggio (max 200 caratteri).';
COMMENT ON COLUMN polls.description         IS 'Descrizione del sondaggio (max 4000 caratteri).';
COMMENT ON COLUMN polls.type                IS 'Tipo: SINGLE_CHOICE o MULTIPLE_CHOICE.';
COMMENT ON COLUMN polls.status              IS 'Stato: DRAFT, PUBLISHED, CLOSED, ARCHIVED.';
COMMENT ON COLUMN polls.multiple_choice     IS 'Flag che abilita la selezione multipla di opzioni.';
COMMENT ON COLUMN polls.anonymous_results   IS 'Se true, i risultati non mostrano chi ha votato cosa.';
COMMENT ON COLUMN polls.opens_at            IS 'Timestamp di apertura del sondaggio.';
COMMENT ON COLUMN polls.closes_at           IS 'Timestamp di chiusura del sondaggio (deve essere > opens_at).';
COMMENT ON COLUMN polls.created_by_user_id  IS 'UUID dell''utente creatore del sondaggio.';
COMMENT ON COLUMN polls.created_at          IS 'Timestamp di creazione.';
COMMENT ON COLUMN polls.updated_at          IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN polls.deleted_at          IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: poll_options
-- Opzioni di risposta per i sondaggi.
-- =============================================================================

CREATE TABLE poll_options (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_id     UUID        NOT NULL
        REFERENCES polls(id),
    label       VARCHAR(500) NOT NULL,
    position    INTEGER     NOT NULL
        CONSTRAINT chk_poll_option_position CHECK (position >= 0),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    deleted_at  TIMESTAMPTZ
);

COMMENT ON TABLE  poll_options            IS 'Opzioni di risposta associate a un sondaggio.';
COMMENT ON COLUMN poll_options.id         IS 'Identificativo univoco dell''opzione.';
COMMENT ON COLUMN poll_options.poll_id    IS 'FK verso polls.id.';
COMMENT ON COLUMN poll_options.label      IS 'Testo dell''opzione (max 500 caratteri).';
COMMENT ON COLUMN poll_options.position   IS 'Ordine di visualizzazione (>= 0).';
COMMENT ON COLUMN poll_options.created_at IS 'Timestamp di creazione.';
COMMENT ON COLUMN poll_options.updated_at IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN poll_options.deleted_at IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: poll_responses
-- Risposta di un utente a un sondaggio (una risposta per utente per sondaggio).
-- Le opzioni selezionate sono in poll_response_options.
-- =============================================================================

CREATE TABLE poll_responses (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_id      UUID        NOT NULL
        REFERENCES polls(id),
    user_id      UUID        NOT NULL,
    submitted_at TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL,
    deleted_at   TIMESTAMPTZ,
    CONSTRAINT uk_poll_response_poll_user UNIQUE (poll_id, user_id)
);

COMMENT ON TABLE  poll_responses              IS 'Risposte degli utenti ai sondaggi. Vincolo: una risposta per utente per sondaggio.';
COMMENT ON COLUMN poll_responses.id           IS 'Identificativo univoco della risposta.';
COMMENT ON COLUMN poll_responses.poll_id      IS 'FK verso polls.id.';
COMMENT ON COLUMN poll_responses.user_id      IS 'UUID dell''utente che ha risposto.';
COMMENT ON COLUMN poll_responses.submitted_at IS 'Timestamp effettivo di invio della risposta.';
COMMENT ON COLUMN poll_responses.created_at   IS 'Timestamp di creazione del record.';
COMMENT ON COLUMN poll_responses.updated_at   IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN poll_responses.deleted_at   IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: poll_response_options
-- Tabella di giunzione: opzioni selezionate per ciascuna risposta.
-- =============================================================================

CREATE TABLE poll_response_options (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_response_id  UUID NOT NULL
        REFERENCES poll_responses(id),
    poll_option_id    UUID NOT NULL
        REFERENCES poll_options(id),
    created_at        TIMESTAMPTZ NOT NULL,
    updated_at        TIMESTAMPTZ NOT NULL,
    deleted_at        TIMESTAMPTZ
);

COMMENT ON TABLE  poll_response_options                  IS 'Tabella di giunzione che mappa le opzioni selezionate a ciascuna risposta al sondaggio.';
COMMENT ON COLUMN poll_response_options.id               IS 'Identificativo univoco del record.';
COMMENT ON COLUMN poll_response_options.poll_response_id IS 'FK verso poll_responses.id.';
COMMENT ON COLUMN poll_response_options.poll_option_id   IS 'FK verso poll_options.id.';
COMMENT ON COLUMN poll_response_options.created_at       IS 'Timestamp di creazione.';
COMMENT ON COLUMN poll_response_options.updated_at       IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN poll_response_options.deleted_at       IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: status_history
-- Audit trail dei cambi di stato su feedback e feature request.
-- =============================================================================

CREATE TABLE status_history (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    target_type         VARCHAR(30)  NOT NULL
        CONSTRAINT chk_status_history_target_type
            CHECK (target_type IN ('FEATURE_REQUEST', 'FEEDBACK_ENTRY')),
    target_id           UUID         NOT NULL,
    old_status          VARCHAR(30)  NOT NULL,
    new_status          VARCHAR(30)  NOT NULL,
    changed_by_user_id  UUID         NOT NULL,
    public_note         VARCHAR(1000),
    internal_note       VARCHAR(1000),
    changed_at          TIMESTAMPTZ  NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,
    deleted_at          TIMESTAMPTZ
);

COMMENT ON TABLE  status_history                     IS 'Audit trail immutabile dei cambiamenti di stato su feedback e feature request.';
COMMENT ON COLUMN status_history.id                  IS 'Identificativo univoco del record di storico.';
COMMENT ON COLUMN status_history.target_type         IS 'Tipo di entità: FEATURE_REQUEST o FEEDBACK_ENTRY.';
COMMENT ON COLUMN status_history.target_id           IS 'UUID dell''entità di cui si registra il cambio stato.';
COMMENT ON COLUMN status_history.old_status          IS 'Stato precedente alla transizione.';
COMMENT ON COLUMN status_history.new_status          IS 'Nuovo stato dopo la transizione.';
COMMENT ON COLUMN status_history.changed_by_user_id  IS 'UUID dell''utente che ha effettuato la modifica.';
COMMENT ON COLUMN status_history.public_note         IS 'Nota visibile pubblicamente (max 1000 caratteri).';
COMMENT ON COLUMN status_history.internal_note       IS 'Nota interna riservata al team (max 1000 caratteri).';
COMMENT ON COLUMN status_history.changed_at          IS 'Timestamp effettivo del cambio di stato.';
COMMENT ON COLUMN status_history.created_at          IS 'Timestamp di creazione del record.';
COMMENT ON COLUMN status_history.updated_at          IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN status_history.deleted_at          IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- TABELLA: moderation_reports
-- Segnalazioni di contenuti da parte degli utenti, con risoluzione da moderatori.
-- =============================================================================

CREATE TABLE moderation_reports (
    id                    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_user_id      UUID        NOT NULL,
    target_type           VARCHAR(30) NOT NULL
        CONSTRAINT chk_moderation_target_type
            CHECK (target_type IN ('FEATURE', 'SCREEN', 'MODULE', 'WORKFLOW', 'GENERIC',
                                   'FEATURE_REQUEST', 'FEEDBACK_ENTRY', 'POLL')),
    target_id             UUID        NOT NULL,
    reason                VARCHAR(100)  NOT NULL,
    details               VARCHAR(2000),
    status                VARCHAR(20) NOT NULL
        CONSTRAINT chk_moderation_status
            CHECK (status IN ('OPEN', 'REVIEWED', 'ACTIONED', 'REJECTED')),
    resolved_at           TIMESTAMPTZ,
    resolved_by_user_id   UUID,
    created_at            TIMESTAMPTZ NOT NULL,
    updated_at            TIMESTAMPTZ NOT NULL,
    deleted_at            TIMESTAMPTZ
);

COMMENT ON TABLE  moderation_reports                      IS 'Segnalazioni di contenuti inappropriati inviate dagli utenti, gestite da moderatori.';
COMMENT ON COLUMN moderation_reports.id                   IS 'Identificativo univoco della segnalazione.';
COMMENT ON COLUMN moderation_reports.reporter_user_id     IS 'UUID dell''utente che ha inviato la segnalazione.';
COMMENT ON COLUMN moderation_reports.target_type          IS 'Tipo di contenuto segnalato.';
COMMENT ON COLUMN moderation_reports.target_id            IS 'UUID del contenuto segnalato.';
COMMENT ON COLUMN moderation_reports.reason               IS 'Motivo della segnalazione (max 100 caratteri).';
COMMENT ON COLUMN moderation_reports.details              IS 'Dettagli aggiuntivi forniti dall''utente (max 2000 caratteri).';
COMMENT ON COLUMN moderation_reports.status               IS 'Stato: OPEN, REVIEWED, ACTIONED, REJECTED.';
COMMENT ON COLUMN moderation_reports.resolved_at          IS 'Timestamp di risoluzione della segnalazione.';
COMMENT ON COLUMN moderation_reports.resolved_by_user_id  IS 'UUID del moderatore che ha risolto la segnalazione.';
COMMENT ON COLUMN moderation_reports.created_at           IS 'Timestamp di creazione.';
COMMENT ON COLUMN moderation_reports.updated_at           IS 'Timestamp dell''ultimo aggiornamento.';
COMMENT ON COLUMN moderation_reports.deleted_at           IS 'Timestamp di cancellazione logica.';

-- =============================================================================
-- INDICI
-- =============================================================================

-- feedback_entries: ricerche per target (target_type + target_id è il pattern più comune)
CREATE INDEX idx_feedback_target
    ON feedback_entries (target_type, target_id);

-- feature_requests: ordinamento e filtro per stato + data di creazione
CREATE INDEX idx_feature_request_status_created
    ON feature_requests (status, created_at DESC);

-- comments: recupero commenti di un target con gestione del threading
CREATE INDEX idx_comments_target_parent
    ON comments (target_type, target_id, parent_comment_id);

-- polls: filtro per stato e finestra temporale (query sondaggi attivi)
CREATE INDEX idx_polls_status_window
    ON polls (status, opens_at, closes_at);

-- status_history: recupero storico di un'entità in ordine cronologico inverso
CREATE INDEX idx_status_history_target
    ON status_history (target_type, target_id, changed_at DESC);
