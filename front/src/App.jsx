import AppRoutes from './routes/routes';
import './App.css';
import { UsersProvider } from './context/UsersContext'; 
import { PostsProvider } from './context/PostsContext';
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
