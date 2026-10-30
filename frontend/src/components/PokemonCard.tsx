import { Link } from 'react-router-dom';
import type { PokemonListItem } from '../types';

interface Props {
  pokemon: PokemonListItem;
}

export default function PokemonCard({ pokemon }: Props) {
  return (
    <Link
      to={`/pokemon/${pokemon.name}`}
      className="bg-white rounded-xl shadow hover:shadow-lg transition-shadow p-4 flex flex-col items-center gap-2 group"
    >
      {pokemon.spriteUrl ? (
        <img
          src={pokemon.spriteUrl}
          alt={pokemon.name}
          className="w-24 h-24 object-contain group-hover:scale-110 transition-transform"
        />
      ) : (
        <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center text-3xl">?</div>
      )}
      <div className="text-center">
        <p className="font-semibold capitalize text-gray-800">{pokemon.name}</p>
        <p className="text-xs text-gray-500">{pokemon.category}</p>
        <p className="text-xs text-gray-400">{(pokemon.massHg / 10).toFixed(1)} kg</p>
      </div>
      <div className="flex flex-wrap gap-1 justify-center">
        {pokemon.skills.slice(0, 3).map((skill) => (
          <span
            key={skill}
            className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full capitalize"
          >
            {skill}
          </span>
        ))}
      </div>
    </Link>
  );
}
