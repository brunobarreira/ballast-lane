import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchPokemonList } from '../api/pokemon';
import PokemonCard from '../components/PokemonCard';

export default function PokemonListPage() {
  const [page, setPage] = useState(0);
  const pageSize = 20;

  const { data, isLoading, isError } = useQuery({
    queryKey: ['pokemon-list', page],
    queryFn: () => fetchPokemonList(page, pageSize),
  });

  if (isLoading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-600"></div>
    </div>
  );

  if (isError) return (
    <div className="text-center py-16 text-red-600">
      <p className="text-xl">Failed to load Pokémon. Please try again.</p>
    </div>
  );

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-800 mb-2">Pokémon</h1>
      <p className="text-gray-500 mb-6">{data?.totalElements.toLocaleString()} Pokémon total</p>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {data?.content.map((pokemon) => (
          <PokemonCard key={pokemon.id} pokemon={pokemon} />
        ))}
      </div>

      <div className="flex justify-center items-center gap-4 mt-8">
        <button
          onClick={() => setPage((p) => Math.max(0, p - 1))}
          disabled={page === 0}
          className="px-4 py-2 bg-red-600 text-white rounded-lg disabled:opacity-40 hover:bg-red-700 transition-colors"
        >
          ← Previous
        </button>
        <span className="text-gray-600">
          Page {page + 1} of {data?.totalPages ?? 1}
        </span>
        <button
          onClick={() => setPage((p) => p + 1)}
          disabled={!data || page >= data.totalPages - 1}
          className="px-4 py-2 bg-red-600 text-white rounded-lg disabled:opacity-40 hover:bg-red-700 transition-colors"
        >
          Next →
        </button>
      </div>
    </div>
  );
}
