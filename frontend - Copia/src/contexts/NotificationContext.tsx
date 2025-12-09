import React, { createContext, useContext, useState, useCallback } from 'react';

interface Notification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

interface NotificationContextType {
  showSuccess: (message: string) => void;
  showError: (message: string) => void;
  showWarning: (message: string) => void;
  showInfo: (message: string) => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within NotificationProvider');
  }
  return context;
};

export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  const addNotification = useCallback((message: string, type: Notification['type']) => {
    const id = Date.now();
    setNotifications(prev => [...prev, { id, message, type }]);

    setTimeout(() => {
      setNotifications(prev => prev.filter(n => n.id !== id));
    }, 4000);
  }, []);

  const showSuccess = useCallback((message: string) => addNotification(message, 'success'), [addNotification]);
  const showError = useCallback((message: string) => addNotification(message, 'error'), [addNotification]);
  const showWarning = useCallback((message: string) => addNotification(message, 'warning'), [addNotification]);
  const showInfo = useCallback((message: string) => addNotification(message, 'info'), [addNotification]);

  const getBackgroundColor = (type: Notification['type']) => {
    switch (type) {
      case 'success': return 'bg-green-500';
      case 'error': return 'bg-red-500';
      case 'warning': return 'bg-yellow-500';
      case 'info': return 'bg-blue-500';
      default: return 'bg-gray-500';
    }
  };

  const getIcon = (type: Notification['type']) => {
    switch (type) {
      case 'success': return '✓';
      case 'error': return '✗';
      case 'warning': return '⚠';
      case 'info': return 'ℹ';
      default: return '';
    }
  };

  return (
    <NotificationContext.Provider value={{ showSuccess, showError, showWarning, showInfo }}>
      {children}
      
      {/* Container de notificações - POSIÇÃO TOP CENTER */}
      <div className="fixed top-4 left-1/2 transform -translate-x-1/2 z-50 flex flex-col gap-2 w-full max-w-md px-4">
        {notifications.map((notification) => (
          <div
            key={notification.id}
            className={`${getBackgroundColor(notification.type)} text-white px-6 py-4 rounded-lg shadow-2xl 
                       animate-slideDown flex items-center gap-3 border-2 border-white`}
          >
            <span className="text-2xl font-bold">{getIcon(notification.type)}</span>
            <span className="flex-1 font-semibold text-lg">{notification.message}</span>
          </div>
        ))}
      </div>
    </NotificationContext.Provider>
  );
};
