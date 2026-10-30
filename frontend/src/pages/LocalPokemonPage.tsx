import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { fetchLocalPokemon, updateLocalPokemon, deleteLocalPokemon } from '../api/pokemon';
import type { LocalPokemon } from '../types';
import { useNavigate } from 'react-router-dom';

function ConfirmModal({ name, onConfirm, onCancel }: { name: string; onConfirm: () => void; onCancel: () => void }) {
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 px-4">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm text-center">
        <div className="text-4xl mb-3">🗑️</div>
        <h2 className="text-lg font-bold mb-2">Remove Pokémon?</h2>
        <p className="text-gray-500 text-sm mb-6 capitalize">
          Remove <strong>{name}</strong> from your local database?
        </p>
        <div className="flex gap-3">
          <button
            onClick={onConfirm}
            className="flex-1 bg-red-600 hover:bg-red-700 text-white py-2 rounded-lg font-semibold transition-colors"
          >
            Remove
          </button>
          <button
            onClick={onCancel}
            className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2 rounded-lg transition-colors"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}

function EditModal({ pokemon, onClose }: { pokemon: LocalPokemon; onClose: () => void }) {
  const [localName, setLocalName] = useState(pokemon.localName || '');
  const [region, setRegion] = useState(pokemon.region || '');
  const [tagsInput, setTagsInput] = useState(pokemon.tags.join(', '));
  const [error, setError] = useState('');
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: () =>
      updateLocalPokemon(pokemon.id, {
        localName: localName || undefined,
        region: region || undefined,
        tags: tagsInput ? tagsInput.split(',').map((t) => t.trim()).filter(Boolean) : [],
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['local-pokemon'] });
      onClose();
    },
    onError: () => setError('Update failed. Check your input.'),
  });

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 px-4">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-md">
        <h2 className="text-xl font-bold capitalize mb-4">Edit {pokemon.name}</h2>
        {error && <p className="text-red-600 text-sm mb-3">{error}</p>}
        <div className="space-y-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Local Name</label>
            <input
              type="text"
              value={localName}
              onChange={(e) => setLocalName(e.target.value)}
              maxLength={100}
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Region</label>
            <input
              type="text"
              value={region}
              onChange={(e) => setRegion(e.target.value)}
              maxLength={100}
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tags (comma-separated)</label>
            <input
              type="text"
              value={tagsInput}
              onChange={(e) => setTagsInput(e.target.value)}
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
        </div>
        <div className="flex gap-3 mt-5">
          <button
            onClick={() => mutation.mutate()}
            disabled={mutation.isPending}
            className="flex-1 bg-green-600 hover:bg-green-700 text-white py-2 rounded-lg transition-colors disabled:opacity-50"
          >
            {mutation.isPending ? 'Saving...' : 'Save'}
          </button>
          <button onClick={onClose} className="flex-1 bg-gray-100 hover:bg-gray-200 py-2 rounded-lg transition-colors">
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}

export default function LocalPokemonPage() {
  const [editing, setEditing] = useState<LocalPokemon | null>(null);
  const [deleting, setDeleting] = useState<LocalPokemon | null>(null);
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const { data, isLoading, isError } = useQuery({
    queryKey: ['local-pokemon'],
    queryFn: fetchLocalPokemon,
  });

  const deleteMutation = useMutation({
    mutationFn: deleteLocalPokemon,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['local-pokemon'] }),
  });

  if (isLoading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
    </div>
  );

  if (isError) return (
    <div className="text-center py-16 text-red-600">Failed to load your Pokémon.</div>
  );

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      {editing && <EditModal pokemon={editing} onClose={() => setEditing(null)} />}
      {deleting && (
        <ConfirmModal
          name={deleting.name}
          onConfirm={() => { deleteMutation.mutate(deleting.id); setDeleting(null); }}
          onCancel={() => setDeleting(null)}
        />
      )}
      <h1 className="text-3xl font-bold text-gray-800 mb-2">My Pokémon</h1>
      <p className="text-gray-500 mb-6">{data?.length ?? 0} Pokémon synced to local database</p>

      {data?.length === 0 && (
        <div className="text-center py-16 text-gray-400">
          <p className="text-xl mb-2">No Pokémon synced yet!</p>
          <button onClick={() => navigate('/')} className="text-red-600 hover:underline">Browse and sync some →</button>
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {data?.map((pokemon) => (
          <div key={pokemon.id} className="bg-white rounded-xl shadow p-4 flex gap-4 items-center">
            {pokemon.spriteUrl && (
              <img src={pokemon.spriteUrl} alt={pokemon.name} className="w-16 h-16 object-contain" />
            )}
            <div className="flex-1 min-w-0">
              <p className="font-semibold capitalize">{pokemon.name}</p>
              {pokemon.localName && (
                <p className="text-sm text-blue-600">"{pokemon.localName}"</p>
              )}
              {pokemon.region && (
                <p className="text-xs text-gray-500">📍 {pokemon.region}</p>
              )}
              <div className="flex flex-wrap gap-1 mt-1">
                {pokemon.tags.map((tag) => (
                  <span key={tag} className="text-xs bg-yellow-100 text-yellow-700 px-2 py-0.5 rounded-full">{tag}</span>
                ))}
              </div>
            </div>
            <div className="flex flex-col gap-2">
              <button
                onClick={() => setEditing(pokemon)}
                className="text-xs bg-blue-100 hover:bg-blue-200 text-blue-700 px-3 py-1 rounded-lg transition-colors"
              >
                Edit
              </button>
              <button
                onClick={() => setDeleting(pokemon)}
                className="text-xs bg-red-100 hover:bg-red-200 text-red-700 px-3 py-1 rounded-lg transition-colors"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
