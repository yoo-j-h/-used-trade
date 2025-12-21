import AppRoutes from './routes/routes';
import './App.css';
import { UsersProvider } from './context/UserContext'; 
import { PostsProvider } from './context/PostContext';
import { CommentsProvider } from './context/CommentsContext';

function App() {
  return (
    <UsersProvider>   
      <PostsProvider>
        <CommentsProvider>
          <AppRoutes />
        </CommentsProvider>
      </PostsProvider>
    </UsersProvider>
  );
}

export default App;
