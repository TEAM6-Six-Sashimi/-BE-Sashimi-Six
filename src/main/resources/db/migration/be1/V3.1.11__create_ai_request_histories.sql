CREATE TABLE ai_request_histories (
                                      ai_request_history_id BIGINT NOT NULL AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      feature_type VARCHAR(50) NOT NULL,
                                      status VARCHAR(30) NOT NULL,
                                      request_snapshot_json JSON NULL,
                                      result_json JSON NULL,
                                      error_message TEXT NULL,
                                      created_at DATETIME(6) NOT NULL,
                                      completed_at DATETIME(6) NULL,
                                      PRIMARY KEY (ai_request_history_id),
                                      INDEX idx_ai_request_histories_created_at (created_at),
                                      INDEX idx_ai_request_histories_feature_created_at (feature_type, created_at),
                                      INDEX idx_ai_request_histories_user_feature_created_at (user_id, feature_type, created_at)
);