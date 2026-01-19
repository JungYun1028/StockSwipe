import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { MessageCircle, X, Send, Sparkles, Loader2 } from 'lucide-react';
import { useApp } from '../context/AppContext';
import { stockAPI } from '../services/api';
import styles from './FloatingChatbot.module.css';

const FloatingChatbot = () => {
  const { isChatOpen, setIsChatOpen, chatMessages, addChatMessage, chatContext, clearChat } = useApp();
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);
  
  const suggestedQuestions = chatContext?.type === 'stock' && chatContext.data
    ? [
        'RSI가 높다는 건 무슨 뜻이야?',
        '이 종목은 단기 투자야, 장기야?',
        '현재 시장 상황은 어때?',
      ]
    : [
        '어떤 도움을 받을 수 있어?',
        '투자 전략 추천해줘',
        '오늘 시장 이슈가 뭐야?',
      ];
  
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [chatMessages]);
  
  useEffect(() => {
    if (isChatOpen && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isChatOpen]);
  
  const handleSend = async () => {
    if (!input.trim() || isLoading) return;
    
    const userMessage = input.trim();
    setInput('');
    addChatMessage('user', userMessage);
    setIsLoading(true);
    
    try {
      // 종목 컨텍스트 생성
      let stockContext = '';
      if (chatContext?.type === 'stock' && chatContext.data) {
        const stock = chatContext.data;
        stockContext = `종목명: ${stock.name}, 종목코드: ${stock.ticker}, 현재가: ${stock.currentPrice}원, RSI: ${stock.rsi || 50}`;
      }
      
      // 실제 OpenAI API 호출
      const response = await stockAPI.chat(userMessage, stockContext);
      addChatMessage('assistant', response.message);
    } catch (error) {
      console.error('챗봇 API 오류:', error);
      addChatMessage('assistant', '죄송해요, 잠시 오류가 발생했어요. 다시 시도해주세요.');
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleSuggestedQuestion = (question) => {
    setInput(question);
    setTimeout(() => handleSend(), 100);
  };
  
  return (
    <>
      {/* Floating Button */}
      <motion.button
        className={styles.floatingButton}
        onClick={() => setIsChatOpen(!isChatOpen)}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        animate={{ rotate: isChatOpen ? 180 : 0 }}
      >
        {isChatOpen ? <X size={24} /> : <MessageCircle size={24} />}
      </motion.button>
      
      {/* Chat Panel */}
      <AnimatePresence>
        {isChatOpen && (
          <motion.div
            className={styles.chatPanel}
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            transition={{ duration: 0.25 }}
          >
            {/* Header */}
            <div className={styles.chatHeader}>
              <div className={styles.headerInfo}>
                <Sparkles size={18} className={styles.sparkle} />
                <span>AI 투자 어시스턴트</span>
              </div>
              <button className={styles.clearButton} onClick={clearChat}>
                대화 초기화
              </button>
            </div>
            
            {/* Context indicator */}
            {chatContext?.type === 'stock' && chatContext.data && (
              <div className={styles.contextBar}>
                📊 {chatContext.data.name} ({chatContext.data.ticker}) 분석 중
              </div>
            )}
            
            {/* Messages */}
            <div className={styles.messagesContainer}>
              {chatMessages.length === 0 ? (
                <div className={styles.welcomeMessage}>
                  <span className={styles.welcomeIcon}>👋</span>
                  <h3>안녕하세요!</h3>
                  <p>주식에 대해 궁금한 점을 물어보세요</p>
                  
                  <div className={styles.suggestedQuestions}>
                    {suggestedQuestions.map((q, i) => (
                      <button
                        key={i}
                        className={styles.suggestedButton}
                        onClick={() => handleSuggestedQuestion(q)}
                      >
                        {q}
                      </button>
                    ))}
                  </div>
                </div>
              ) : (
                <>
                  {chatMessages.map((msg, index) => (
                    <motion.div
                      key={index}
                      className={`${styles.message} ${msg.role === 'user' ? styles.userMessage : styles.assistantMessage}`}
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.1 }}
                    >
                      <div className={styles.messageContent}>
                        {msg.content.split('\n').map((line, i) => (
                          <span key={i}>
                            {line}
                            {i < msg.content.split('\n').length - 1 && <br />}
                          </span>
                        ))}
                      </div>
                    </motion.div>
                  ))}
                  {isLoading && (
                    <div className={`${styles.message} ${styles.assistantMessage}`}>
                      <div className={styles.loadingIndicator}>
                        <Loader2 size={16} className={styles.spinner} />
                        <span>분석 중...</span>
                      </div>
                    </div>
                  )}
                </>
              )}
              <div ref={messagesEndRef} />
            </div>
            
            {/* Input */}
            <div className={styles.inputContainer}>
              <input
                ref={inputRef}
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                placeholder="질문을 입력하세요..."
                className={styles.input}
                disabled={isLoading}
              />
              <button
                className={styles.sendButton}
                onClick={handleSend}
                disabled={!input.trim() || isLoading}
              >
                <Send size={18} />
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
};

export default FloatingChatbot;

