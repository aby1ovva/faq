-- Create faqs table
CREATE TABLE IF NOT EXISTS faqs (
    id BIGSERIAL PRIMARY KEY,
    question VARCHAR(2000) NOT NULL,
    answer VARCHAR(2000) NOT NULL,
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster search
CREATE INDEX idx_faqs_category ON faqs(category);
CREATE INDEX idx_faqs_question ON faqs USING GIN (to_tsvector('russian', question));
CREATE INDEX idx_faqs_answer ON faqs USING GIN (to_tsvector('russian', answer));

-- Insert sample data
INSERT INTO faqs (question, answer, category) VALUES
('Как добавить новый вопрос?', 'Используйте POST запрос на /api/faqs с JSON телом, содержащим question, answer и category.', 'general'),
('Как работает поиск?', 'Поиск ищет совпадения в текстах вопросов и ответов. Используйте параметр q в GET запросе.', 'technical'),
('Можно ли редактировать вопросы?', 'Да, отправьте PUT запрос на /api/faqs/{id} с обновленными данными.', 'general'),
('Как удалить вопрос?', 'Отправьте DELETE запрос на /api/faqs/{id} для удаления вопроса.', 'general'),
('Как связаться с поддержкой?', 'Вы можете написать нам на support@example.com или позвонить по телефону +7 (xxx) xxx-xx-xx.', 'support'),
('Какие способы оплаты доступны?', 'Мы принимаем банковские карты (Visa, MasterCard, Мир), электронные кошельки и банковские переводы.', 'billing');

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_faqs_updated_at BEFORE UPDATE ON faqs
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Grant privileges (adjust username as needed)
-- GRANT ALL PRIVILEGES ON DATABASE faq_db TO postgres;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;