export interface Notification {
    id: string;
    recipientId: number;
    senderId: number;
    type: string;
    postId: number;
    message: string;
    isRead: boolean;
    createdAt: string;
}
