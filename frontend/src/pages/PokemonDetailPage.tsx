import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchPokemonDetail, syncPokemon } from '../api/pokemon';
import { useAuth } from '../contexts/AuthContext';

function Toast({ message, type }: { message: string; type: 'success' | 'error' }) {
  return (
    <div className={`fixed bottom-6 right-6 z-50 px-5 py-3 rounded-xl shadow-lg text-white font-medium transition-all
      ${type === 'success' ? 'bg-green-600' : 'bg-red-600'}`}>
      {type === 'success' ? '✅' : '❌'} {message}
    </div>
  );
}

export default function PokemonDetailPage() {
  const { nameOrId } = useParams<{ nameOrId: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const queryClient = useQueryClient();
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  const showToast = (message: string, type: 'success' | 'error') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000);
  };

  const { data, isLoading, isError } = useQuery({
    queryKey: ['pokemon-detail', nameOrId],
    queryFn: () => fetchPokemonDetail(nameOrId!),
    enabled: !!nameOrId,
  });

  const syncMutation = useMutation({
    mutationFn: () => syncPokemon(nameOrId!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['local-pokemon'] });
      showToast(`${nameOrId} synced to your database!`, 'success');
    },
    onError: () => showToast('Failed to sync Pokémon', 'error'),
  });

  if (isLoading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
    </div>
  );

  if (isError || !data) return (
    <div className="text-center py-16 text-red-600">
      <p className="text-xl">Pokémon not found.</p>
      <button onClick={() => navigate(-1)} className="mt-4 text-blue-600 hover:underline">Go back</button>
    </div>
  );

  const statColors: Record<string, string> = {
    hp: 'bg-green-500', attack: 'bg-red-500', defense: 'bg-blue-500',
    'special-attack': 'bg-purple-500', 'special-defense': 'bg-indigo-500', speed: 'bg-yellow-500',
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      {toast && <Toast message={toast.message} type={toast.type} />}
      <button onClick={() => navigate(-1)} className="text-blue-600 hover:underline mb-4 block">← Back</button>

      <div className="bg-white rounded-2xl shadow-md p-6">
        <div className="flex flex-col sm:flex-row gap-6 items-center">
          {data.spriteUrl && (
            <img src={data.spriteUrl} alt={data.name} className="w-40 h-40 object-contain" />
          )}
          <div className="flex-1">
            <div className="flex items-center justify-between flex-wrap gap-2">
              <h1 className="text-3xl font-bold capitalize">{data.name}</h1>
              <span className="text-gray-400 text-lg">#{data.id}</span>
            </div>
            {data.description && (
              <p className="text-gray-600 mt-2 italic">{data.description}</p>
            )}
            {isAuthenticated && (
              <button
                onClick={() => syncMutation.mutate()}
                disabled={syncMutation.isPending}
                className="mt-4 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg transition-colors disabled:opacity-50"
              >
                {syncMutation.isPending ? 'Syncing...' : '💾 Sync to My Database'}
              </button>
            )}
          </div>
        </div>

        <div className="mt-6">
          <h2 className="text-lg font-semibold mb-3">Base Stats</h2>
          <div className="space-y-2">
            {Object.entries(data.stats).map(([stat, value]) => (
              <div key={stat} className="flex items-center gap-3">
                <span className="capitalize text-sm text-gray-600 w-36">{stat}</span>
                <div className="flex-1 bg-gray-100 rounded-full h-3">
                  <div
                    className={`h-3 rounded-full ${statColors[stat] || 'bg-gray-500'}`}
                    style={{ width: `${Math.min(100, (value / 255) * 100)}%` }}
                  />
                </div>
                <span className="text-sm font-medium w-8 text-right">{value}</span>
              </div>
            ))}
          </div>
        </div>

        {data.evolutionChain.length > 0 && (
          <div className="mt-6">
            <h2 className="text-lg font-semibold mb-3">Evolution Chain</h2>
            <div className="flex items-center gap-2 flex-wrap">
              {data.evolutionChain.map((name, i) => (
                <div key={name} className="flex items-center gap-2">
                  {i > 0 && <span className="text-gray-400">→</span>}
                  <button
                    onClick={() => navigate(`/pokemon/${name}`)}
                    className="capitalize bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded-full text-sm transition-colors"
                  >
                    {name}
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
